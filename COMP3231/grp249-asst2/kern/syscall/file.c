#include <types.h>
#include <kern/errno.h>
#include <kern/fcntl.h>
#include <kern/limits.h>
#include <kern/stat.h>
#include <kern/seek.h>
#include <lib.h>
#include <uio.h>
#include <proc.h>
#include <current.h>
#include <synch.h>
#include <vfs.h>
#include <vnode.h>
#include <file.h>
#include <syscall.h>
#include <copyinout.h>
#include <limits.h>

/*
    Group 249: Assignment 2 - COMP3231 - 2042 T1
*/

/*
    File System Data Structures
*/

// Global Open File Table             (An array of max size MAX_OPEN_FILES(128), each element is of type struct open_file)
struct open_file *global_filearray;

// Per-Process File Descriptor Table  (A linked-List with each node holding a struct proc_element data type)
struct proc_element *process_list;



/*
    File System Calls
*/


ssize_t sys_read(int fd, userptr_t buf, size_t nBytes, size_t *read_bytes) {
    check_process();
    /*
        1. Check if fd is valid, throw appropriate error if not
        2. Use get_proc_fp to get the open_file struct
        3. Once found, get the vnode and create an uio in preparation for VOP_READ
        4. Use VOP_READ to get the information from the file to the uio
        6. Make sure offsets are adjusted for in vnode

        On success return 0, read_bytes = number of bytes read
        On EOF return 0, read_bytes = 0
        On error return error code, read_bytes = -1
    */

    if (fd < 0 || fd >= MAX_OPEN_FILES) {
        *read_bytes = -1;
        return EBADF;
    }

    if (buf == NULL) {
        *read_bytes = -1;
        return EFAULT;
    }

    struct open_file *file = get_proc_fp(fd);
    if (file == NULL) {
        kprintf("READ: File not found!\n");
        *read_bytes = -1;
        return EBADF;
    }

    struct vnode *vnode_ptr = file->v_ptr;
    struct uio ui;
    struct iovec iov;

    uio_uinit(&iov, &ui, buf, nBytes, file->offset, UIO_READ);
    
    int err = VOP_READ(vnode_ptr, &ui);

    if (err) {
        kprintf("READ: VOP_READ returned error\n");
        *read_bytes = -1;
        return err;
    }

    // Update offset and amount of bytes read
    file->offset = ui.uio_offset;
    *read_bytes = nBytes - ui.uio_resid; // EOF case is potentially not accounted for

    return 0;
};

ssize_t sys_write(int fd, userptr_t buf, size_t nBytes, size_t *written_bytes) {
    check_process();
    //kprintf("The passed in file descriptor: %d", fd);
    /*
        1. Check if fd is valid, throw appropriate error if not
        2. Use get_proc_fp to get the open_file struct
        3. Once found, get the vnode and create an uio in preparation for VOP_WRITE
        4. Use VOP_WRITE
        6. Make sure offsets are adjusted for in vnode

        On success return 0, written_bytes = number of bytes written
        On EOF return 0, written_bytes = 0
        On error return error code, written_bytes = -1
    */
    if (fd < 0 || fd >= MAX_OPEN_FILES) {
        *written_bytes = -1;
        return EBADF;
    }

    if (buf == NULL) {
        *written_bytes = -1;
        return EFAULT;
    }
    
    struct open_file *file = get_proc_fp(fd);
    if (file == NULL) {
        kprintf("WRITE: File couldn't be found!\n");
        *written_bytes = -1;
        return EBADF;
    }
    struct vnode *vnode_ptr = file->v_ptr;
    struct uio ui;
    struct iovec iov;

    uio_uinit(&iov, &ui, buf, nBytes, file->offset, UIO_WRITE);
    
    int err = VOP_WRITE(vnode_ptr, &ui);

    if (err) {
        kprintf("WRITE: VOP_WRITE returned error\n");
        *written_bytes = -1;
        return err;
    }

    // Update offset and written_bytes
    file->offset = ui.uio_offset;
    *written_bytes = nBytes - ui.uio_resid; // EOF case is potentially not accounted for

    return 0;
};

off_t sys_lseek(int fd, off_t pos, int whence, off_t *new_pos) {
    check_process();
    /*
        1. Error checking, fd, pos, whence
            a. Also use VOP_ISSEEKABLE
        2. Get the open_file struct with fd
        3. Adjust the offset position depending on whence flag (switch)
        
        On success return 0, new_pos = new position
        On error return error code, new_pos = -1
    */

    if (fd < 0 || fd >= MAX_OPEN_FILES) {
        *new_pos = -1;
        return EBADF;
    }

    // error checking with VOP_ISSEEKABLE
    struct open_file *file = get_proc_fp(fd);
    if (file == NULL) {
        kprintf("LSEEK: File couldn't be found!\n");
        *new_pos = -1;
        return EBADF;
    }

    if (!VOP_ISSEEKABLE(file->v_ptr)) {
        kprintf("Object not seekable\n");
        *new_pos = -1;
        return ESPIPE;
    }


    switch (whence) {
    case SEEK_SET:
        if (pos < 0) {
            *new_pos = -1;
            return EINVAL;
        }
        file->offset = pos;
        *new_pos = file->offset;

        break;
    case SEEK_CUR:
        if (pos + file->offset < 0) {
            *new_pos = -1;
            return EINVAL;
        }
        
        file->offset = pos + file->offset;
        *new_pos = file->offset;

        break;
    case SEEK_END: ;
        struct stat *file_stat = kmalloc(sizeof(struct stat));
        if (file_stat == NULL) {
            kprintf("LSEEK: Unable to allocate for struct stat");
            *new_pos = -1;
            return ENOMEM;
        }

        int err = VOP_STAT(file->v_ptr, file_stat);
        if (err) {
            kprintf("VOP_STAT errored");
            *new_pos = -1;
            kfree(file_stat);
            return EBADF;
        }

        off_t end_pos = file_stat->st_size;

        if (end_pos + file->offset < 0) {
            *new_pos = -1;
            kfree(file_stat);
            return EINVAL;
        }

        file->offset = pos + end_pos;
        *new_pos = file->offset;
        kfree(file_stat);

        break;
    default:
        kprintf("Invalid value for whence\n");
        *new_pos = -1;
        return EINVAL;
    }

    return 0;
};

int sys_open(const_userptr_t filename, int flags, mode_t mode) {
    check_process();
    //kprintf("Starting sys_open()=====================\n");
    //kprintf("Flag passed in: %d\n", flags);
    int error_check = 0;

    // Copy the user-level address string to a safe kernel-level destination
    char *path = kmalloc(sizeof(char) * (PATH_MAX + NAME_MAX) / 4);
    size_t *actual_length = kmalloc(sizeof(size_t));
    size_t len = PATH_MAX + NAME_MAX;
    error_check = copyinstr(filename, path, len, actual_length); 
    if (error_check != 0) {
        kprintf("copyinstr Error: %d\n", error_check);
        return error_check;
    }
    //kprintf("Given Path: %s\n", path);

    struct vnode *current_vnode = kmalloc(sizeof(struct vnode));
    if (current_vnode == NULL) {
        panic("Unable to successfully allocate memory for vnode pointer\n");
    }

    // Use vfs interface to get current_vnode to point at the appropriate vnode
    error_check = vfs_open(path, flags, mode, &current_vnode);
    if (error_check != 0) {
        kprintf("vfs_open() Error: %d\n", error_check);
        return error_check;
    }
    // Create an open_file element in the global file array to store information about this new open file
    // E.g. Offset and Vnode Pointer 
    int index = -1; 
    for (int open_file = 0; open_file < MAX_OPEN_FILES; open_file++) {
        if (global_filearray[open_file].v_ptr == NULL) {
                global_filearray[open_file].v_ptr = current_vnode;
                global_filearray[open_file].times_used = 1;
                
                if (flags & O_APPEND) {
                    struct stat* new_stat = kmalloc(sizeof(struct stat*));
                    if (new_stat == NULL) {panic("Unable to successfully allocate memory for VOP_STAT\n");}
                    VOP_STAT(current_vnode, new_stat);
                    global_filearray[open_file].offset = new_stat->st_size;
                } else {
                    global_filearray[open_file].offset = 0;
                }
            index = open_file;
            break;
        }
    }

    // Return ENFILE Error if Global File Table is Full
    if (index == -1) {
        return ENFILE;
    }
    //kprintf("Current Global Index: %d\n", index);


    // 3. Put an element in the process array
    // Iterate through process linked list to see if the current process already has been initialised
    struct proc_element *curr = process_list;
    struct proc_element *found_proc = NULL;
    struct proc_element *previous = curr;
    struct proc *current_process = curproc;
    while (curr != NULL) {
        if (current_process == curr->process) {
            found_proc = curr;
            break;
        }
        previous = curr;
        curr = curr->next_process;
    }

    // If process doesnt have its own proc_element, create it and add it to the linked list.
    if (curr == NULL) {
        struct proc_element *new_element = kmalloc(sizeof(struct proc_element));
        new_element->fd_array = kmalloc(sizeof(struct open_file *) * MAX_OPEN_FILES);
        if (new_element == NULL || new_element->fd_array == NULL) {
            panic("Unable to create a new array for the current process\n");
        }

        new_element->fd_array[1] = &global_filearray[1];
        new_element->fd_array[2] = &global_filearray[2];
        new_element->process = curproc;
        new_element->next_process = NULL;
        found_proc = new_element;
    
        // Ensure linked list is linked together properly
        if (curr == NULL && previous == NULL) {
            process_list = new_element;
        } else if (curr == NULL) {
            previous->next_process = new_element;
        }
    }

    // Add the fd to this process's fd array
    int search = 0;
    for (search = 0; search < MAX_OPEN_FILES; search++) {
        if (found_proc->fd_array[search] == NULL) {
            found_proc->fd_array[search] = &global_filearray[index];
            break;
        }
    }

    // Return EMFILE error if process table was too full to store another fd
    if (search == MAX_OPEN_FILES) {
         kprintf("EMFILE Error\n");
        return EMFILE;
    }


    // To differentiate a returning file handler and a returning error value
    // All file handler values will be negative when returned
    //kprintf("Return fd for sys_open: %d\n", search);
    return search * -1;
};


// ============================================================= sys_close() ================================================================
int sys_close(int fd) {
    check_process();
    //kprintf("fd: %d\n", fd);

    // Might need some code for exceptions to do with special 0, 1, 2
    // E.g. Just close the process element and not the global file element

    // 1. Get the open_file associated with this fd
    struct open_file *current_file = get_proc_fp(fd);
    if (current_file == NULL) {
        return EBADF;
    }

    // Remove the fd from processList
    get_current_proc()->fd_array[fd] = NULL;

    // Run vfs_vlose() to decrease the vnode refcount
    int initialRefCount = current_file->v_ptr->vn_refcount;
    vfs_close(current_file->v_ptr);
    current_file->times_used -= 1;

    // Check for the EIO error
    if (initialRefCount > 1 && (current_file->v_ptr->vn_refcount != initialRefCount - 1)) {
        kprintf("sys_close(): Hard I/O Error\n");
        return EIO;
    }

    // If refcount is 0 close the open_file
    // Remove open_file from Global File Table if this is the last reference to it
    if (current_file->times_used == 0) {
        current_file->offset = 0;
        current_file->v_ptr = NULL;
    }

    //kprintf("Successful Close\n");
    return 0;
};

int sys_dup2(int oldfd, int newfd) {
    check_process();
    if (oldfd == newfd) {
        return newfd;
    }

    if (oldfd < 0 || newfd < 0 || oldfd > 127 || newfd > 127) {
        return EBADF;
    }

    // 0. Check that oldfd actually is valid (open)
    struct proc_element *current_process = get_current_proc();
    if (current_process == NULL) {
        kprintf("current process was null");
        return EBADF;
    }

    if (current_process->fd_array[oldfd] == NULL) {
        kprintf("current array file pointer was null");
        return EBADF;
    }

    // 1. If the newfd is currently in use, close it before re-pointing it
    if (get_proc_fp(newfd) != NULL) {
        kprintf("current Vnode ptr was null");
        sys_close(newfd);
    }

    // 2. newfd now points to the same open file as oldfd
    struct open_file *file = get_proc_fp(oldfd);
    current_process->fd_array[newfd] = file;
    file->times_used += 1;
    return newfd * -1;
};


void check_process() {
    // Check that the current process is not already within the process linked-list
    if (get_current_proc() == NULL) {
        // Initialise the current process to the process list
        struct proc_element *new_element = kmalloc(sizeof(struct proc_element));
        new_element->fd_array = kmalloc(sizeof(struct open_file *) * MAX_OPEN_FILES);
        if (new_element == NULL || new_element->fd_array == NULL) {
            panic("Unable to create a new array for the current process\n");
        }

        new_element->fd_array[1] = &global_filearray[1];
        new_element->fd_array[2] = &global_filearray[2];
        new_element->process = curproc;
        new_element->next_process = NULL;
        process_list = new_element;
    }
}

void file_bootstrap() {
    kprintf("Allocating memory for File Table Arrays\n");
    // Global Open File Table
    global_filearray = kmalloc(sizeof(struct open_file) * MAX_OPEN_FILES);
    process_list = NULL;

    // Check that memory allocation was successful
    if (global_filearray == NULL) {
        panic("Unable to create File Table arrays");
    }

    // Special File Descriptor 0-STDIN, 1-STDOUT, 2-STDERR assumed always open
    // 1. Create a vnode to the right place 
    // 2. Set a open_file to look at the vnode
    // 3. Whenever a new process is created, chuck pointers the the right open files in index's 0, 1 and 2

    struct vnode *stdout = kmalloc(sizeof(struct vnode));
    struct vnode *stderr = kmalloc(sizeof(struct vnode));

    mode_t fake_mode = 0;
    char terminal[5];
 
    strcpy(terminal,"con:");
    int result1 = vfs_open(terminal, O_RDWR, fake_mode, &stdout);
    strcpy(terminal,"con:"); 
    int result2 = vfs_open(terminal, O_RDWR, fake_mode, &stderr); 

    if (result1 != 0 || result2 != 0) {
        panic("Unsuccessful File Descriptor 1 and 2 init");
    }

    global_filearray[1].v_ptr = stdout;
    global_filearray[2].v_ptr = stderr;
};

/*
    Helper Functions
*/

// Description in file.h
struct proc_element* get_current_proc() {
    // Get the current process element in process_list
    struct proc_element *curr = process_list;
    struct proc_element *found_proc = NULL;
    struct proc *current_process = curproc;
    while (curr != NULL) {
        //kprintf("Checking... (This Process: %s  The LL Process: %s)", curproc->p_name, curr->process->p_name);
        if (current_process == curr->process) {
            found_proc = curr;
            break;
        }
        curr = curr->next_process;
    }
    return found_proc;
};

// Description in file.h
struct open_file *get_proc_fp(int proc_fd) {

    // EBADF - Inital check to see if fd is possible
    if (proc_fd < 0 || proc_fd >= MAX_OPEN_FILES) {
        kprintf("get_proc_fp: fd out of range");
        return NULL;
    } 

    // Check if the index refers to a valid open file
    struct proc_element *current_process = get_current_proc();
    if (current_process == NULL) {
        kprintf("Current_process was null\n");
        return NULL;
    }

    if (current_process->fd_array[proc_fd] == NULL) {
        return NULL;
    }

    return current_process->fd_array[proc_fd];
};
