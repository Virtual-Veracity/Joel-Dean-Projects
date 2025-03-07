// Assignment 2 19T3 COMP1511: Pokedex
// test_pokedex.c
//
// This file allows you to automatically test the functions you
// implement in pokedex.c.
//
// This program was written by Joel Dean (z5208947)
// on 14/11/19 - ##/11/19
//
// Version 1.0.0: Assignment released.

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#include "pokedex.h"

// Add your own #defines here.


// Sample data on Bulbasaur, the Pokemon with pokemon_id 1.
#define BULBASAUR_ID 1
#define BULBASAUR_NAME "Bulbasaur"
#define BULBASAUR_HEIGHT 0.7
#define BULBASAUR_WEIGHT 6.9
#define BULBASAUR_FIRST_TYPE GRASS_TYPE
#define BULBASAUR_SECOND_TYPE POISON_TYPE

// Sample data on Ivysaur, the Pokemon with pokemon_id 2.
#define IVYSAUR_ID 2
#define IVYSAUR_NAME "Ivysaur"
#define IVYSAUR_HEIGHT 1.0
#define IVYSAUR_WEIGHT 13.0
#define IVYSAUR_FIRST_TYPE GRASS_TYPE
#define IVYSAUR_SECOND_TYPE POISON_TYPE

// Sample data on Venusaur, the Pokemon with pokemon_id 3.
#define VENUSAUR_ID 3
#define VENUSAUR_NAME "Venusaur"
#define VENUSAUR_HEIGHT 2.0
#define VENUSAUR_WEIGHT 100.0
#define VENUSAUR_FIRST_TYPE GRASS_TYPE
#define VENUSAUR_SECOND_TYPE POISON_TYPE

// Sample data on Butterfree, the Pokemon with pokemon_id 12.
#define BUTTERFREE_ID 12
#define BUTTERFREE_NAME "Butterfree"
#define BUTTERFREE_HEIGHT 1.1
#define BUTTERFREE_WEIGHT 32.0
#define BUTTERFREE_FIRST_TYPE FLYING_TYPE
#define BUTTERFREE_SECOND_TYPE BUG_TYPE

// Sample data on Starly, the Pokemon with pokemon_id 101.
#define STARLY_ID 101
#define STARLY_NAME "Starly"
#define STARLY_HEIGHT 0.3
#define STARLY_WEIGHT 2.3
#define STARLY_FIRST_TYPE FLYING_TYPE
#define STARLY_SECOND_TYPE NONE_TYPE



// Add your own prototypes here.


// Tests for Pokedex functions from pokedex.c.
static void test_new_pokedex(void);
static void test_add_pokemon(void);
static void test_get_found_pokemon(void);
static void test_next_pokemon(void);
static void test_evolution(void);
static void test_stage3(void);

// Helper functions for creating/comparing Pokemon.
static Pokemon create_bulbasaur(void);
static Pokemon create_ivysaur(void);
static Pokemon create_venusaur(void);
static Pokemon create_butterfree(void);
static Pokemon create_starly(void);
static int is_same_pokemon(Pokemon first, Pokemon second);
static int is_copied_pokemon(Pokemon first, Pokemon second);



int main(int argc, char *argv[]) {
    printf("Welcome to the COMP1511 Pokedex Tests!\n");

    printf("\n==================== Pokedex Tests ====================\n");

    test_new_pokedex();
    test_add_pokemon();
    test_next_pokemon();
    test_stage3();
    test_evolution();
    
    
    
    

    printf("\nAll Pokedex tests passed, you are Awesome!\n");
}


////////////////////////////////////////////////////////////////////////
//                     Pokedex Test Functions                         //
////////////////////////////////////////////////////////////////////////

// `test_new_pokedex` checks whether the new_pokedex and destroy_pokedex
// functions work correctly, to the extent that it can.
//
// It does this by creating a new Pokedex, checking that it's not NULL,
// then calling destroy_pokedex.
//
// Note that it isn't possible to check whether destroy_pokedex has
// successfully destroyed/freed the Pokedex, so the best we can do is to
// call the function and make sure that it doesn't crash..
static void test_new_pokedex(void) {
    printf("\n>> Testing new_pokedex\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("       --> Checking that the returned Pokedex is not NULL\n");
    assert(pokedex != NULL);

    printf("    ... Destroying the Pokedex\n");
    destroy_pokedex(pokedex);

    printf(">> Passed new_pokedex tests!\n");
}

// `test_add_pokemon` checks whether the add_pokemon function works
// correctly.
//
// It does this by creating the Pokemon Bulbasaur (using the helper
// functions in this file and the provided code in pokemon.c), and
// calling add_pokemon to add it to the Pokedex.
//
// Some of the ways that you could extend these test would include:
//   - adding additional Pokemon other than just Bulbasaur,
//   - checking whether the currently selected Pokemon is correctly set,
//   - checking that functions such as `count_total_pokemon` return the
//     correct result after more Pokemon are added,
//   - ... and more!
static void test_add_pokemon(void) {
    printf("\n>> Testing add_pokemon\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("    ... Creating Bulbasaur\n");
    Pokemon bulbasaur = create_bulbasaur();

    printf("    ... Adding Bulbasaur to the Pokedex\n");
    add_pokemon(pokedex, bulbasaur);

    printf("    ... Destroying the Pokedex\n");
    destroy_pokedex(pokedex);

    printf(">> Passed add_pokemon tests!\n");
}

// `test_next_pokemon` checks whether the next_pokemon function works
// correctly.
//
// It does this by creating two Pokemon: Bulbasaur and Ivysaur (using
// the helper functions in this file and the provided code in pokemon.c).
//
// It then adds these to the Pokedex, then checks that calling the
// next_pokemon function changes the currently selected Pokemon from
// Bulbasaur to Ivysaur.
//
// Some of the ways that you could extend these tests would include:
//   - adding even more Pokemon to the Pokedex,
//   - calling the next_pokemon function when there is no "next" Pokemon,
//   - calling the next_pokemon function when there are no Pokemon in
//     the Pokedex,
//   - ... and more!
static void test_next_pokemon(void) {
    printf("\n>> Testing next_pokemon\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("    ... Creating Bulbasaur and Ivysaur\n");
    Pokemon bulbasaur = create_bulbasaur();
    Pokemon ivysaur = create_ivysaur();

    printf("    ... Adding Bulbasaur and Ivysaur to the Pokedex\n");
    add_pokemon(pokedex, bulbasaur);
    add_pokemon(pokedex, ivysaur);

    printf("       --> Checking that the current Pokemon is Bulbasaur\n");
    assert(is_same_pokemon(get_current_pokemon(pokedex), bulbasaur));

    printf("    ... Moving to the next pokemon\n");
    next_pokemon(pokedex);

    printf("       --> Checking that the current Pokemon is Ivysaur\n");
    assert(is_same_pokemon(get_current_pokemon(pokedex), ivysaur));

    printf("    ... Destroying the Pokedex\n");
    destroy_pokedex(pokedex);

    printf(">> Passed next_pokemon tests!\n");
}

// `test_get_found_pokemon` checks whether the get_found_pokemon
// function works correctly.
//
// It does this by creating two Pokemon: Bulbasaur and Ivysaur (using
// the helper functions in this file and the provided code in pokemon.c).
//
// It then adds these to the Pokedex, sets Bulbasaur to be found, and
// then calls the get_found_pokemon function to get all of the Pokemon
// which have been found (which should be just the one, Bulbasaur).
//
// Some of the ways that you could extend these tests would include:
//   - calling the get_found_pokemon function on an empty Pokedex,
//   - calling the get_found_pokemon function on a Pokedex where none of
//     the Pokemon have been found,
//   - checking that the Pokemon in the new Pokedex are in ascending
//     order of pokemon_id (regardless of the order that they appeared
//     in the original Pokedex),
//   - checking that the currently selected Pokemon in the returned
//     Pokedex has been set correctly,
//   - checking that the original Pokedex has not been modified,
//   - ... and more!
static void test_get_found_pokemon(void) {
    printf("\n>> Testing get_found_pokemon\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("    ... Creating Bulbasaur and Ivysaur\n");
    Pokemon bulbasaur = create_bulbasaur();
    Pokemon ivysaur = create_ivysaur();

    printf("    ... Adding Bulbasaur and Ivysaur to the Pokedex\n");
    add_pokemon(pokedex, bulbasaur);
    add_pokemon(pokedex, ivysaur);

    printf("       --> Checking that the current Pokemon is Bulbasaur\n");
    assert(get_current_pokemon(pokedex) == bulbasaur);
    
    printf("    ... Setting Bulbasaur to be found\n");
    find_current_pokemon(pokedex);

    printf("    ... Getting all found Pokemon\n");
    Pokedex found_pokedex = get_found_pokemon(pokedex);

    printf("       --> Checking the correct Pokemon were copied and returned\n");
    assert(count_total_pokemon(found_pokedex) == 1);
    assert(count_found_pokemon(found_pokedex) == 1);
    assert(is_copied_pokemon(get_current_pokemon(found_pokedex), bulbasaur));

    printf("    ... Destroying both Pokedexes\n");
    destroy_pokedex(pokedex);
    destroy_pokedex(found_pokedex);

    printf(">> Passed get_found_pokemon tests!\n");
}


//Test Stage 3 Functions + Prev pokemon
static void test_stage3(void) {
    printf("\n>> Testing Stage 3\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("    ... Creating Bulbasaur + Ivysaur + Venusaur + Starly "
           " + Butterfree\n");
    Pokemon bulbasaur = create_bulbasaur();
    Pokemon ivysaur = create_ivysaur();
    Pokemon venusaur = create_venusaur();
    Pokemon butterfree = create_butterfree();
    Pokemon starly = create_starly();

    printf("    ... Adding all created pokemon to the Pokedex\n");
    add_pokemon(pokedex, bulbasaur);
    add_pokemon(pokedex, butterfree);
    add_pokemon(pokedex, ivysaur);
    add_pokemon(pokedex, starly);
    add_pokemon(pokedex, venusaur);
    printf("\n");
    
    printf(">> Testing Show types\n");
    printf("    ... Unique types include poison grass flying bug\n");
    show_types(pokedex);
    printf("\n");
    
    printf(">> Testing Exploring\n");
    printf("    ... Many overlaps - Only Bulbausaur and Butterfree "
    "should be found\n");
    go_exploring(pokedex);
    print_pokemon(pokedex);
    printf("\n");
    
    printf(">>Testing Count Total Pokemon\n");
    printf("    ... Counting Total - Answer should be 5\n");
    printf("%d\n", count_total_pokemon(pokedex));
    
    printf("    ... Removing current pokemon and Counting -> Answer = 4\n");
    remove_pokemon(pokedex);    
    printf("%d\n", count_total_pokemon(pokedex));
    printf("\n");
    
    printf(">>Testing Count found pokemon -> Only 2 have been found\n");
    find_current_pokemon(pokedex);
    next_pokemon(pokedex);
    find_current_pokemon(pokedex);
    printf("%d\n", count_found_pokemon(pokedex));
}
    

//Test stage 4 functions + Find Current pokemon + Next Pokemon + Change Current
// + Remove pokemon
static void test_evolution(void) {
    printf("\n>> Testing get_next_evolution\n");

    printf("    ... Creating a new Pokedex\n");
    Pokedex pokedex = new_pokedex();

    printf("    ... Creating Bulbasaur + Ivysaur + Venusaur + Starly "
           " + Butterfree\n");
    Pokemon bulbasaur = create_bulbasaur();
    Pokemon ivysaur = create_ivysaur();
    Pokemon venusaur = create_venusaur();
    Pokemon butterfree = create_butterfree();
    Pokemon starly = create_starly();

    printf("    ... Adding all created pokemon to the Pokedex\n");
    add_pokemon(pokedex, bulbasaur);
    add_pokemon(pokedex, butterfree);
    add_pokemon(pokedex, ivysaur);
    add_pokemon(pokedex, starly);
    add_pokemon(pokedex, venusaur);
    printf("\n");
    
    printf("    ...Connected evolutions and testing print evolutions\n");
    add_pokemon_evolution(pokedex, BULBASAUR_ID, IVYSAUR_ID);
    add_pokemon_evolution(pokedex, IVYSAUR_ID, VENUSAUR_ID);
    
    printf(">>Testing get next evolution\n");
    printf("    ... Test valid case\n");
    get_next_evolution(pokedex);
    printf("    ... Test no evolution case\n");
    next_pokemon(pokedex);
    get_next_evolution(pokedex);
    printf("    ... Test Empty pokedex case at end - File should exit\n");
    
    
    
    printf(
    "    ... Ensuring pokemon evolutions works as well as print evolutions "
    " >>With no found pokemon\n");
    printf(
    "     -- Should show bulbasur -> ivysaur -> venusaur but only with ID's\n");
    show_evolutions(pokedex);
    printf("\n");
    
    printf("    ... Test print evolution + Find pokemon\n");
    printf("     -- Bulbasaur should show name\n");
    find_current_pokemon(pokedex);
    show_evolutions(pokedex);
    printf("\n");
    
    printf("    ... Test print evolution + change current pokemon\n");
    printf("     -- Venusaur should show name\n");
    change_current_pokemon(pokedex, VENUSAUR_ID);
    find_current_pokemon(pokedex);
    change_current_pokemon(pokedex, BULBASAUR_ID);
    show_evolutions(pokedex);
    printf("\n");
    
    printf("    ... Test print evolution\n" 
    "            -- Ivysaur should show name\n");
    change_current_pokemon(pokedex, IVYSAUR_ID);
    find_current_pokemon(pokedex);
    change_current_pokemon(pokedex, BULBASAUR_ID);
    show_evolutions(pokedex);
    printf("\n");
    
    printf("    ... Test for a pokemon with no evolutions\n");
    printf("    ... Tests print evolutions and next pokemon\n");
    printf("    Empty space should be beneath this");
    next_pokemon(pokedex);
    show_evolutions(pokedex);
    printf("\n");
    
    printf(">>Testing Remove pokemon + show evolution for empty pokedex\n");
    printf("    ... Remove all pokemon in pokedex\n");
    int i = 0;
    while (i < 5) {
        remove_pokemon(pokedex);
        i++;
    }
    printf("    ... Show evolutions\n");
    printf("No output is expected\n");
    show_evolutions(pokedex);
    
    printf(">> Test get next evolution\n    ... Test empty pokedex case\n");
    printf("    -- Program should exit\n");
    get_current_pokemon(pokedex);
    
}


////////////////////////////////////////////////////////////////////////
//                     Helper Functions                               //
////////////////////////////////////////////////////////////////////////

// Helper function to create Bulbasaur for testing purposes.
static Pokemon create_bulbasaur(void) {
    Pokemon pokemon = new_pokemon(
            BULBASAUR_ID, BULBASAUR_NAME,
            BULBASAUR_HEIGHT, BULBASAUR_WEIGHT,
            BULBASAUR_FIRST_TYPE,
            BULBASAUR_SECOND_TYPE
    );
    return pokemon;
}

// Helper function to create Ivysaur for testing purposes.
static Pokemon create_ivysaur(void) {
    Pokemon pokemon = new_pokemon(
            IVYSAUR_ID, IVYSAUR_NAME,
            IVYSAUR_HEIGHT, IVYSAUR_WEIGHT,
            IVYSAUR_FIRST_TYPE,
            IVYSAUR_SECOND_TYPE
    );
    return pokemon;
}

static Pokemon create_venusaur(void) {
    Pokemon pokemon = new_pokemon(
            VENUSAUR_ID, VENUSAUR_NAME,
            VENUSAUR_HEIGHT, VENUSAUR_WEIGHT,
            VENUSAUR_FIRST_TYPE,
            VENUSAUR_SECOND_TYPE
    );
    return pokemon;
}

static Pokemon create_butterfree(void) {
    Pokemon pokemon = new_pokemon(
            BUTTERFREE_ID, BUTTERFREE_NAME,
            BUTTERFREE_HEIGHT, BUTTERFREE_WEIGHT,
            BUTTERFREE_FIRST_TYPE,
            BUTTERFREE_SECOND_TYPE
    );
    return pokemon;
}

static Pokemon create_starly(void) {
    Pokemon pokemon = new_pokemon(
            STARLY_ID, STARLY_NAME,
            STARLY_HEIGHT, STARLY_WEIGHT,
            STARLY_FIRST_TYPE,
            STARLY_SECOND_TYPE
    );
    return pokemon;
}


// Helper function to compare whether two Pokemon are the same.
// This checks that the two pointers contain the same address, i.e.
// they are both pointing to the same pokemon struct in memory.
//
// Pokemon ivysaur = new_pokemon(0, 'ivysaur', 1.0, 13.0, GRASS_TYPE, POISON_TYPE)
// Pokemon also_ivysaur = ivysaur
// is_same_pokemon(ivysaur, also_ivysaur) == TRUE
static int is_same_pokemon(Pokemon first, Pokemon second) {
    return first == second;
}

// Helper function to compare whether one Pokemon is a *copy* of
// another, based on whether their attributes match (e.g. pokemon_id,
// height, weight, etc).
// 
// It also checks that the pointers do *not* match -- i.e. that the
// pointers aren't both pointing to the same pokemon struct in memory.
// If the pointers both contain the same address, then the second
// Pokemon is not a *copy* of the first Pokemon.
// 
// This function doesn't (yet) check that the Pokemon's names match
// (but perhaps you could add that check yourself...).
static int is_copied_pokemon(Pokemon first, Pokemon second) {
    return (pokemon_id(first) == pokemon_id(second))
    &&  (first != second)
    &&  (pokemon_height(first) == pokemon_height(second))
    &&  (pokemon_weight(first) == pokemon_weight(second))
    &&  (pokemon_first_type(first) == pokemon_first_type(second))
    &&  (pokemon_second_type(first) == pokemon_second_type(second));
}

// Write your own helper functions here!
