/*
 * Copyright (c) 2000, 2001, 2002, 2003, 2004, 2005, 2008, 2009
 *	The President and Fellows of Harvard College.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE UNIVERSITY AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

#include <types.h>
#include <kern/errno.h>
#include <lib.h>
#include <spl.h>
#include <spinlock.h>
#include <current.h>
#include <mips/tlb.h>
#include <addrspace.h>
#include <vm.h>
#include <proc.h>

/*
 * Note! If OPT_DUMBVM is set, as is the case until you start the VM
 * assignment, this file is not compiled or linked or in any way
 * used. The cheesy hack versions in dumbvm.c are used instead.
 *
 * UNSW: If you use ASST3 config as required, then this file forms
 * part of the VM subsystem.
 *
 */

struct addrspace *as_create(void) {
	struct addrspace *as;
	as = kmalloc(sizeof(struct addrspace));
	if (as == NULL) {
		return NULL;
	}

  // =========== Page Table ==============
  // Allocate memory for the first level of the page table
  as->pageTable = kmalloc(sizeof(paddr_t *) * PAGE_PARENT_SIZE);
  if (as->pageTable == NULL) {
    return NULL;
  }

  // Set every top-level index to point at null
  for (int i = 0; i < PAGE_PARENT_SIZE; i++) {
    as->pageTable[i] = NULL;
  }

  // =========== Region List ==============
  as->regionList = NULL;

  // kprintf("Creating The Kernels Page Table Parent\n");
	return as;
}

int as_copy(struct addrspace *old, struct addrspace **ret) {
	struct addrspace *newAddressSpace;
  // kprintf("Copy stuff\n");
	newAddressSpace = as_create();
	if (newAddressSpace == NULL) {
		return ENOMEM;
	}

  // =========== Copy Page Table ==============
  // Iterate through each parent index to see if any child tables have been created
	for (int parentIndex = 0; parentIndex < PAGE_PARENT_SIZE; parentIndex++) {
    if (old->pageTable[parentIndex] != NULL) {
      newAddressSpace->pageTable[parentIndex] = kmalloc(sizeof(paddr_t) * PAGE_CHILD_SIZE);

      // For any child tables, iterate through and copy over the pageTableEntry's to the new address space
      paddr_t *childTable = old->pageTable[parentIndex];
      for (int childIndex = 0; childIndex < PAGE_CHILD_SIZE; childIndex++) {
        if (childTable[childIndex] != 0) {
          newAddressSpace->pageTable[parentIndex][childIndex] = childTable[childIndex];
        } else {
          newAddressSpace->pageTable[parentIndex][childIndex] = 0;
        }
      }
    }
  }

  // =========== Copy Region List ==============
  struct region *prevRegion = NULL;
  struct region *currRegion = old->regionList;
  while (currRegion != NULL) {

    struct region *copyRegion = kmalloc(sizeof(struct region));
    copyRegion->startVAddress = currRegion->startVAddress;
    copyRegion->nPages = currRegion->nPages;
    copyRegion->tempWrite = currRegion->tempWrite;
    copyRegion->nextRegion = NULL;

    if (prevRegion == NULL) {
      newAddressSpace->regionList = copyRegion;
      prevRegion = copyRegion;
    } else {
      prevRegion->nextRegion = copyRegion;
      prevRegion = copyRegion;
    }

    currRegion = currRegion->nextRegion;
  }

	*ret = newAddressSpace;
	return 0;
}

void
as_destroy(struct addrspace *as)
{
	/*
	 * Clean up as needed.
	 */
   // kprintf("Runing as_destroy\n");

    // ============= Free Page Table ================
   // Iterate through each parent index 
	for (int parentIndex = 0; parentIndex < PAGE_PARENT_SIZE; parentIndex++) {
    if (as->pageTable[parentIndex] != 0) {
      kfree(as->pageTable[parentIndex]);
    }
  }
  kfree(as->pageTable);

  // kprintf("Destory Region List");

  // ============= Free Region List ================
  struct region *currRegion = as->regionList;
	struct region *prevRegion = NULL;
	while (currRegion != NULL) {
    prevRegion = currRegion;
		currRegion = currRegion->nextRegion;
    kfree(prevRegion);
	}

  kfree(as);
}

void
as_activate(void)
{
	struct addrspace *as;
  int i, spl;

	as = proc_getas();
	if (as == NULL) {
		/*
		 * Kernel thread without an address space; leave the
		 * prior address space in place.
		 */
		return;
	}

  // Pretty sure this is a flush of the TLB
	/* Disable interrupts on this CPU while frobbing the TLB. */
	spl = splhigh();

	for (i=0; i<NUM_TLB; i++) {
		tlb_write(TLBHI_INVALID(i), TLBLO_INVALID(), i);
	}

	splx(spl);
}

void
as_deactivate(void)
{
	/*
	 * Write this. For many designs it won't need to actually do
	 * anything. See proc.c for an explanation of why it (might)
	 * be needed.
	 */
   as_activate();
}

/*
 * Set up a segment at virtual address VADDR of size MEMSIZE. The
 * segment in memory extends from VADDR up to (but not including)
 * VADDR+MEMSIZE.
 *
 * The READABLE, WRITEABLE, and EXECUTABLE flags are set if read,
 * write, or execute permission should be set on the segment. At the
 * moment, these are ignored. When you write the VM system, you may
 * want to implement them.
 */
int
as_define_region(struct addrspace *as, vaddr_t vaddr, size_t memsize,
		 int readable, int writeable, int executable)
{
	/*
	 * Write this.
	 */
  size_t offset = vaddr & ~(vaddr_t)PAGE_FRAME;
  size_t offsetAndMemSize = offset + memsize;
  int numPages = (offsetAndMemSize + PAGE_SIZE - 1) / PAGE_SIZE;
  vaddr_t baseVAddress = vaddr & PAGE_FRAME;
  
  struct region *newRegion = kmalloc(sizeof(struct region));
  if (newRegion == NULL) {
    panic("unable to allocate memory");
  }
  newRegion->startVAddress = baseVAddress;
  newRegion->nPages = numPages;
  newRegion->perms = readable | writeable | executable;
  newRegion->tempWrite = false;
  newRegion->nextRegion = NULL;

  if (as->regionList == NULL) {
    as->regionList = newRegion;
  } else {
    struct region *current = as->regionList;
    struct region *previous = as->regionList;
    while (current != NULL && current->startVAddress < newRegion->startVAddress) {
      previous = current;
      current = current->nextRegion;
    }
    if (current == NULL) {
      previous->nextRegion = newRegion;
    } else {
      newRegion->nextRegion = current->nextRegion;
      current->nextRegion = newRegion;
    }
  }
  // Loop and print the regions - Testing Code
  // struct region *print = as->regionList;
  // while (print != NULL) {
  //   kprintf("VAddressList: %08x\n", print->startVAddress);
  //   kprintf("Number of PagesList: %d\n", print->nPages);
  //   print = print->nextRegion;
  // }
	return 0;
}

int
as_prepare_load(struct addrspace *as)
{
	/*
	 * Write this.
	 */

  // Make Regions writeable for elf_load.c
  struct region *currRegion = as->regionList;
  while (currRegion != NULL) {
    currRegion->tempWrite = true;
    currRegion = currRegion->nextRegion;
  }

	return 0;
}

int
as_complete_load(struct addrspace *as)
{
	/*
	 * Write this.
	 */

  // Make Regions not-writable again
  struct region *currRegion = as->regionList;
  while (currRegion != NULL) {
    currRegion->tempWrite = false;
    currRegion = currRegion->nextRegion;
  }

	/* Flush TLB */
	int spl = splhigh();
	for (int i = 0; i < NUM_TLB; i++) {
		tlb_write(TLBHI_INVALID(i), TLBLO_INVALID(), i);
	}
	splx(spl);

	return 0;
}

int
as_define_stack(struct addrspace *as, vaddr_t *stackptr)
{
	/*
	 * Write this.
	 */

	// Define a stack region with 16*PAGE_SIZE size and permissions
	int error = as_define_region(as, USERSTACK - (16 * PAGE_SIZE), 16 * PAGE_SIZE, 1, 1, 1);
	if (error) {
		return error;
	}

	/* Initial user-level stack pointer */
	*stackptr = USERSTACK;

	return 0;
}

