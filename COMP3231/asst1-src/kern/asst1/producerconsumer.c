/* This file will contain your solution. Modify it as you wish. */
#include <types.h>
#include <lib.h>
#include <synch.h>
#include "producerconsumer.h"

/* The bounded buffer uses an extra free slot to distinguish between
   empty and full. See
   http://www.mathcs.emory.edu/~cheung/Courses/171/Syllabus/8-List/array-queue2.html
   for details if needed. 
*/

#define BUFFER_SIZE (BUFFER_ITEMS + 1)

/* Declare any variables you need here to keep track of and
   synchronise the bounded buffer. The declaration of a buffer is
   shown below. It is an array of pointers to data_items.
*/


data_item_t * item_buffer[BUFFER_SIZE];

volatile int write_head, read_tail;

struct cv *cv_full;
struct cv *cv_empty;
struct lock *lock_mutex;



/* The following functions test if the buffer is full or empty. They
   are obviously not synchronised in any way */
static bool is_full() {
        return (write_head + 1) % BUFFER_SIZE == read_tail;
}

static bool is_empty() {
        return write_head == read_tail;
}
/* consumer_receive() is called by a consumer to request more data. It
   should block on a sync primitive if no data is available in your
   buffer. It should not busy wait! Any concurrency issues involving
   shared state should also be handled. 
*/

data_item_t * consumer_receive(void)
{
        data_item_t * item;

        lock_acquire(lock_mutex);
        while (is_empty()) {
                cv_wait(cv_full, lock_mutex);
        }

        item = item_buffer[read_tail];
        read_tail = (read_tail + 1) % BUFFER_SIZE;

        cv_signal(cv_empty, lock_mutex);
        lock_release(lock_mutex);

        return item;
}

/* producer_send() is called by a producer to store data in the
   bounded buffer.  It should block on a sync primitive if no space is
   available in the buffer. It should not busy wait! Any concurrency
   issues involving shared state should also be handled.
*/

void producer_send(data_item_t *item)
{       
        lock_acquire(lock_mutex);
        while (is_full()) {
                cv_wait(cv_empty, lock_mutex);
        }

        item_buffer[write_head] = item;
        write_head = (write_head + 1) % BUFFER_SIZE;


        cv_signal(cv_full, lock_mutex);
        lock_release(lock_mutex);
}

/* Perform any initialisation (e.g. of global data or synchronisation
   variables) you need here. Note: You can panic if any allocation
   fails during setup
*/

void producerconsumer_startup(void)
{
        lock_mutex = lock_create("mutex");
        cv_full    = cv_create("full");
        cv_empty   = cv_create("empty");
        if (lock_mutex == NULL || cv_full == NULL || cv_empty == NULL) {
                panic("Unable to successfully create primitives");
        }
        write_head = read_tail = 0;
}

/* Perform your clean-up here */
void producerconsumer_shutdown(void)
{
        lock_destroy(lock_mutex);
        cv_destroy(cv_full);
        cv_destroy(cv_empty);
}

