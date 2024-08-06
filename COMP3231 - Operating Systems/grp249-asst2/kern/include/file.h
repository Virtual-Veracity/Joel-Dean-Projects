/*
 * Declarations for file handle and file table management.
 */

#ifndef _FILE_H_
#define _FILE_H_

#define MAX_OPEN_FILES 128

#define SEEK_SET        0
#define SEEK_CUR        1
#define SEEK_END        2

/*
 * Contains some file-related maximum length constants
 */
#include <limits.h>

/*
 ==========         Data Structure Declarations    ==================
*/

struct open_file {
    off_t offset;            // Is the current offset into the file
    struct vnode *v_ptr;    // Is a pointer to the relevant vnode
    int times_used;
};

struct proc_element {
    struct proc* process;          // The memory address of the process
    struct open_file** fd_array;        // The array containing pointers to open_file data types 
    struct proc_element* next_process;  // Its a linked list, so this is the next node in the list
};



/*
 ===========        Function Declarations         ===================
*/

/* 
    File related Functions
*/
void file_bootstrap(void);

/* 
    System Calls
*/
ssize_t sys_read(int fd, userptr_t buf, size_t nBytes, size_t *read_bytes);
ssize_t sys_write(int fd, userptr_t buf, size_t nBytes, size_t *read_bytes);
off_t sys_lseek(int fd, off_t pos, int whence, off_t *new_pos);
int sys_open(const_userptr_t filename, int flags, mode_t mode);
int sys_close(int fd);
int sys_dup2(int oldfd, int newfd);

/* 
    Helper Functions
*/


/*
    get_proc_fp()
    Args:
        - N/A
    Return Value:
        - Success: struct proc_element*
        - Failure: NULL 
    Method:
    Gets the relevant process element for the current process

*/
struct proc_element* get_current_proc(void);

/*
    get_proc_fp()
    Args:
        - proc_index: Is the file handler passed from user-level
    Return Value:
        - Success: 0
        - Failure: NULL (Indicates -> EBADF(30), fd was invalid 
        !! Doesn't check EBADF error to see if file is only open for reading
    Method:
    Obtains the open_file element that is pointed at by the fd

*/
struct open_file *get_proc_fp(int proc_fd);

void check_process(void);


/*
 *    On successful return, zero in a3 register; return value in v0
 *    (v0 and v1 for a 64-bit return value).
 *
 *    On error return, nonzero in a3 register; errno value in v0.
*/

//  Derived from the OS/161 Manual Provided for System Calls
//      -> https://cgi.cse.unsw.edu.au/~cs3231/18s1/os161/man/syscall/

// https://cgi.cse.unsw.edu.au/~cs3231/18s1/os161/man/syscall/read.html



// https://cgi.cse.unsw.edu.au/~cs3231/18s1/os161/man/syscall/write.html


// https://cgi.cse.unsw.edu.au/~cs3231/18s1/os161/man/syscall/lseek.html


/*
    Args: 
        filename: path to the file being opened
        flags: Indicators for how the file should be opened
        mode(optional): Mostly ignore - if provided just pass to vfs instead of flags

        flags {
            Exclusive:
            O_RDONLY	Open for reading only.
            O_WRONLY	Open for writing only.
            O_RDWR	Open for reading and writing.

            Optional:
            O_CREAT	Create the file if it doesn't exist.
            O_EXCL	Fail if the file already exists. (Only works if O_CREAT used in conjunction)
            O_TRUNC	Truncate the file to length 0 upon open.
            O_APPEND	Open the file in append mode.(Write to end of file including any concurrent access)
        }
    Return Value: 
        Success: A filehandle - suitable for read(), write(), close()
            + Must be >= 0
            + 0, 1, 2 are reserved (Assumed always open)

        Errors: -1 (And set errno to appropriate number)
            a. ENODEV: Device Prefix of file not exist
            b. ENOTDIR: Apart of path to file was not a directory
            c. ENOENT: Apart of path did not exist
            d. ENOENT: Name of file doesn exist and O_CREAT not specified
            e. EEXIST: File exists but O_EXCL specified
            f. EISDIR: Object named is a directory and was opened for writing
            g. EMFILE: Process file table full
            h. ENFILE: System file table full
            i. ENXIO: Named object is  a device with no filesystem
            j. ENOSPC: Filesystem is full cannot create file (File was going to be created otherwise)
            k. EINVAL: Invalid flag value/s
            l. EIO: I/O error occurred
            m. EFAULT: filename was an invalid pointer


    1. Need to think about symbolic links - In manual
    2. Is atomic

    
        
*/



/*
    Args: 
        fd - A file handle relating to the file decriptor that is to be closed 

        + Must be non-negative integer
        + Must be smaller than the maximum file handler number if we create a limit on the data structure

    Return Value: 
        Success: 0
        Errors: -1 (And set errno to appropriate number)
            a. EBADF: fd not valid
            b. EIO: An I/O error occurred

    File handle fd is closed
        1. Will be returned by open and dup2 system calls
        2. Doesn't affect any other file handles even if on the same file
        3. Even if vfs operation fails the file handle is still closed and becomes invalid to use
        
*/


/*
    Args: 
        oldfd - The file handler to be copied over
        newfd - The variable which will hold the copy

        + Both must be non-negative integers
        + Must be smaller than the maximum file handler number if we create a limit on the data structure

    Return Value: 
        Success: newfd
        Errors: -1 (And set errno to appropriate number)
            a. EBADF: oldfd/newfd not valid (Not an int or the num isnt valid)
            b. EMFILE: Process fule table full - Limit on open files for process reached
            c. ENFILE: Systems file table is full or global limit reached

    Clone a provided File Descriptor (They share the same pointer to a file including the seek location)
        1. Clones the 'oldfd' into the provided 'newfd' 
        2. If 'newfd' is not empty then the file being pointed to is closed before
            a clone is made.
        3. System Call is atomic - No other process may access while this is running
            - E.g. Cannot close newfd while cloning old fd into it
            - E.g. If 2 threads attempt conflicting calls (dup2(4,3) and dup2(3,4)).
                The result must be the same as if one went first then the next
        4. If dup2 is called with equivalent file handlers then there is no effect
        
*/




#endif /* _FILE_H_ */
