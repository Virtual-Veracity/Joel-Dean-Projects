/* This file will contain your solution. Modify it as you wish. */
#include <types.h>
#include <lib.h>
#include <synch.h>
#include "cdrom.h"


typedef struct buffer {
        struct cv *cv;
        struct lock *lock;
        int block;
        int value;
        bool available;
} bufferElem;

/* Some variables shared between threads */
struct semaphore *semRequest;
struct semaphore *arraySize;
struct lock *arrayLock;
bufferElem bufferArray[MAX_CONCURRENT_REQ];



/*
* cdrom_read: This is the client facing interface to the cdrom that makes
* requests to the cdrom system. 
* 
* Args :-
* block_num: The block number of the cdrom that the client is requesting the
* content from.
*
* This function makes a request to the cdrom system using the cdrom_block_read()
* function. cdrom_block_read() returns immediately before the request has completed.
* cdrom_read() then puts the current thread to sleep until the request completes,
* upon which, this thread needs to the woken up, the value from the specific block
* obtained, and the value is return to the requesting client.
*/

unsigned int cdrom_read(int block_num)
{
        int index = -1;
        int blockValue;

        P(semRequest);
        P(arraySize);

        lock_acquire(arrayLock);
        for (int idx = 0; idx < MAX_CONCURRENT_REQ; idx++) {
                if (bufferArray[idx].available == true) {
                        bufferArray[idx].available = false;
                        bufferArray[idx].block = block_num;
                        index = idx;
                        break;
                }
        }
        if (index == -1) {
                panic("Unable to find a free spot in shared array");
        }
        lock_release(arrayLock);

        lock_acquire(bufferArray[index].lock);
        /*kprintf("Received request read block %d\n",block_num);*/
        cdrom_block_request(block_num);
        cv_wait(bufferArray[index].cv, bufferArray[index].lock);

        blockValue = bufferArray[index].value;
        lock_release(bufferArray[index].lock);
        bufferArray[index].available = true;
        
        V(arraySize);
        return blockValue;
}

/*
* cdrom_handler: This function is called by the system each time a cdrom block request
* has completed.
* 
* Args:-
* block_num: the number of the block originally requested from the cdrom.
* value: the content of the block, i.e. the data value retrieved by the request.
* 
* The cdrom_handler runs in its own thread. The handler needs to deliver the value 
* to the original requestor and wake the requestor waiting on the value.
*/

void cdrom_handler(int block_num, unsigned int value)
{
        (void) block_num;
        (void) value;
        lock_acquire(arrayLock);
        for (int idx = 0; idx < MAX_CONCURRENT_REQ; idx++) {
                if (bufferArray[idx].block == block_num) {
                        lock_acquire(bufferArray[idx].lock);
                        bufferArray[idx].value = value; 
                        cv_signal(bufferArray[idx].cv, bufferArray[idx].lock);
                        lock_release(bufferArray[idx].lock);
                }
        }
        lock_release(arrayLock);
        V(semRequest);
}

/*
* cdrom_startup: This is called at system initilisation time, 
* before the various threads are started. Use it to initialise 
* or allocate anything you need before the system starts.
*/

void cdrom_startup(void)
{
        semRequest = sem_create("request", MAX_CONCURRENT_REQ);
        arraySize = sem_create("request", MAX_CONCURRENT_REQ);
        arrayLock = lock_create("arrayLock");

        if (semRequest == NULL || arrayLock == NULL || arraySize == NULL) {
                panic("Primitive failed to initialise");
        }

        for (int i = 0; i < MAX_CONCURRENT_REQ; i++) {
                bufferArray[i].cv = cv_create("conditionVariable");
                bufferArray[i].lock = lock_create("lock");
                bufferArray[i].available = true;
                
                if (bufferArray[i].cv == NULL || bufferArray->lock == NULL) {
                panic("Array Primitive failed to initialise");
                }
        }
}   

/*
* cdrom_shutdown: This is called after all the threads in the system
* have completed. Use this function to clean up and de-allocate anything
* you set up in cdrom_startup()
*/
void cdrom_shutdown(void)
{
        sem_destroy(semRequest);
        sem_destroy(arraySize);
        lock_destroy(arrayLock);

        for (int i = 0; i < MAX_CONCURRENT_REQ; i++) {
                cv_destroy(bufferArray[i].cv);
                lock_destroy(bufferArray[i].lock);
        }
}


/* Just a sanity check to warn about including the internal header file 
   It contains nothing relevant to a correct solution and any use of 
   what is contained is likely to result in failure in our testing 
   */

#if defined(CDROM_TESTER_H) 
#error Do not include the cdrom_tester header file
#endif