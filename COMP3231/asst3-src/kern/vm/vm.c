#include <types.h>
#include <kern/errno.h>
#include <lib.h>
#include <thread.h>
#include <addrspace.h>
#include <vm.h>
#include <machine/tlb.h>
#include <proc.h>
#include <spl.h>

/* Place your page table functions here */


void vm_bootstrap(void)
{
    /* Initialise any global components of your VM sub-system here.  
     *  
     * You may or may not need to add anything here depending what's
     * provided or required by the assignment spec.
     */
}

int
vm_fault(int faulttype, vaddr_t faultaddress) {
  // Permission Check: If a write occurs on a read-only
  if (faulttype == VM_FAULT_READONLY) {
    return EFAULT;
  
  // Check that the faulttype given to this function is valid, otherwise return EINVAL error
  } else if (faulttype != VM_FAULT_READ && faulttype != VM_FAULT_WRITE) {
    return EINVAL;
  }

  /*
    Cases:
      0. Figure out how to check for an invalid region 
      1. No Page Table for this process
      2. No Child Table
      3. No Page Table Entry
      4. Found the PTE
  */
  
  // 0. Check Valid Region 
  // 1 Means Valid, 2 means Valid and Writeable, 0 means error 
  int validRegion = checkValidRegion(faultaddress);
  if (validRegion == 0) {
    return EFAULT;
  }

  // 1. Check that the Page Table Exists (Should be initialised before calling vm_fault)
  struct addrspace *currentAS = proc_getas();
  if (currentAS == NULL || currentAS->pageTable == NULL) {
    return EFAULT;
  }

  // Initialise Variables 
  paddr_t pKernelAddr = KVADDR_TO_PADDR(faultaddress);
  uint32_t index1 = pKernelAddr >> 21;
  uint32_t index2 = (pKernelAddr << 11) >> 23;
  int result;

  // 2. Check if the Child Table exists
  if (currentAS->pageTable[index1] == NULL) {
    // Level 1 Entry
    result = createChildTable(index1);
    if (result != 0) {return result;}
  }

  // 3. Check for the Page Table Entry (Child Table Entry)
  if (currentAS->pageTable[index1][index2] == 0) {
    result = createPageTableEntry(index1, index2, (validRegion == 2) ? true : false);
    if (result != 0) {return result;}
  }


  // Load the TLB with the Page Table Translation
  uint32_t entryHi = faultaddress & PAGE_FRAME;
  uint32_t entryLow = currentAS->pageTable[index1][index2];
  int spl = splhigh();
  if (tlb_probe(entryHi, entryLow) < 0) {
    tlb_random(entryHi, entryLow);
  }
  splx(spl);

  return 0;
}

// Check if Address exists within a valid Region
int checkValidRegion(vaddr_t address) {
  struct addrspace *currAS = proc_getas();
  struct region *currRegion = currAS->regionList;
  while (currRegion != NULL) {
    if (address >= currRegion->startVAddress && address < currRegion->startVAddress + (currRegion->nPages * PAGE_SIZE)) {
      if (currRegion->tempWrite == true || ((currRegion->perms & WRITE_MASK) > 0)) {
        return 2;
      }
      return 1;
    }
    currRegion = currRegion->nextRegion;
  }
  return 0;
}

int createChildTable(uint32_t parentIndex) {
  paddr_t *newChildTable = kmalloc(sizeof(paddr_t) * PAGE_CHILD_SIZE);
  if (newChildTable == NULL) {return EFAULT;} 
  for (int childIndex = 0; childIndex < PAGE_CHILD_SIZE; childIndex++) {
    newChildTable[childIndex] = 0;
  }
  proc_getas()->pageTable[parentIndex] = newChildTable;
  return 0;
}

int createPageTableEntry (uint32_t parentIndex, uint32_t childIndex, bool dirty) {
  if (dirty == true) {
    proc_getas()->pageTable[parentIndex][childIndex] = TLBLO_VALID | TLBLO_DIRTY | (KVADDR_TO_PADDR(alloc_kpages(1)) & PAGE_FRAME);
  } else {
    proc_getas()->pageTable[parentIndex][childIndex] = (TLBLO_VALID | TLBLO_DIRTY | (KVADDR_TO_PADDR(alloc_kpages(1)) & PAGE_FRAME));
  }
  return 0;
}

/*
 * SMP-specific functions.  Unused in our UNSW configuration.
 */

void
vm_tlbshootdown(const struct tlbshootdown *ts)
{
	(void)ts;
	panic("vm tried to do tlb shootdown?!\n");
}

