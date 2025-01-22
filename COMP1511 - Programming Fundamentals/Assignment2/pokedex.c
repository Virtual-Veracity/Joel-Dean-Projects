// Assignment 2 19T3 COMP1511: Pokedex
// pokedex.c
//
// This program was written by Joel Dean (z5208947)
// on 14/11/19 - ##/11/19
//
// Version 2.0.1: My Assignment
//
// Program will develop the working of a pokedex and will allow a user
// to interact with the pokedex.
// User should be able to add/find/learn details/remove pokemon

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

// Note you are not allowed to use <string.h> in this assignment

// ----------------------------------------------
// Add any extra #includes your code needs here.
// ----------------------------------------------

#include "pokedex.h"


// ----------------------------------------------
// Add your own #defines here.
// ----------------------------------------------

#define MAXSIZE 60
#define FOUND 1
#define HIDDEN 0

// Note you are not permitted to use arrays in struct pokedex,
// you must use a linked list.
//
// The only exception is that char arrays are permitted for
// search_pokemon and functions it may call, as well as the string
// returned by pokemon_name (from pokemon.h).
//
// You will need to add fields to struct pokedex.
// You may change or delete the head field.
struct pokedex {
    struct pokenode *head;
    struct pokenode *current;
};


// You don't have to use the provided struct pokenode, you are free to
// make your own struct instead.
//
// If you use the provided struct pokenode, you will need to add fields
// to it to store other information.
struct pokenode {
    Pokemon pokemon;
    struct pokenode *next;
    int found;
    struct pokenode *evolution;
};


// ----------------------------------------------
// Add your own structs here.
// ----------------------------------------------


// ----------------------------------------------
// Add prototypes for your own functions here.
// ----------------------------------------------

// Creates a new pokenode struct and returns a pointer to it.
static struct pokenode *new_pokenode(Pokemon pokemon);

//Returns the length of a string
int string_length(struct pokenode *string);

//Prints out the Pokemon ID in proper format
void print_pokemonid(struct pokenode *print);

//Will print out error message and exit program if no pokemon are inputted
void no_pokemon(Pokedex pokedex);


// Creates a new Pokedex, and returns a pointer to it.
// Note: you will need to modify this function.
Pokedex new_pokedex(void) {
    // Malloc memory for a new Pokedex, and check that the memory
    // allocation succeeded.
    Pokedex pokedex = malloc(sizeof(struct pokedex));
    assert(pokedex != NULL);

    // Set the head of the linked list to be NULL.
    // (i.e. set the Pokedex to be empty)
    pokedex->head = NULL;

    // TODO: Add your own code here.
    pokedex->current = NULL;
    
    return pokedex;
}

// Create a new pokenode struct and returns a pointer to it.
static struct pokenode *new_pokenode(Pokemon pokemon) {
    //Set aside space for a pokemon slot
    struct pokenode *new_pokenode = malloc(sizeof(struct pokenode));
    new_pokenode->next = NULL;
    new_pokenode->pokemon = pokemon;
    //Start of pokemon as being hidden
    new_pokenode->found = HIDDEN;
    new_pokenode->evolution = NULL;
    
    return new_pokenode;
}

//////////////////////////////////////////////////////////////////////
//                         Stage 1 Functions                        //
//////////////////////////////////////////////////////////////////////

// Add a new Pokemon to the Pokedex.
void add_pokemon(Pokedex pokedex, Pokemon pokemon) {
    
    //Making a new pokemon node
    struct pokenode *new_pokemon = new_pokenode(pokemon);
    
    //Set 1st added pokemon to head of pokedex
    if (pokedex->head == NULL) {
        pokedex->head = new_pokemon;
        pokedex->current = new_pokemon;
        
        // Otherwise add pokemon to end of list
    } else {
        //Make pointer scroll to go through list - previous lags one behind
        struct pokenode *scroll = pokedex->head;
        struct pokenode *previous;
        //Scroll to end of pokedex
        while (scroll != NULL) {
            //Check if pokemon contains duplicate ID
            if (pokemon_id(scroll->pokemon) == pokemon_id(new_pokemon->pokemon)) {
                printf("Duplicate pokemon ID received: ");
                print_pokemonid(scroll);
                printf(" - Invalid\n");
                exit(1);
            }
            previous = scroll;
            scroll = scroll->next;
        }
        //Connect end of pokedex to new pokemon
        previous->next = new_pokemon;
    }
    
}

// Print out the details of the currently selected Pokemon.
void detail_pokemon(Pokedex pokedex) {
    
    //If pokemon is not found only print out ID
    if (pokedex->current->found == HIDDEN) {
        printf("ID: ");
            print_pokemonid(pokedex->current);
            printf("\nName: ");
            
        //Print out the same number of asterisks as the pokemon name
        int counter = 0;
        while (counter < string_length(pokedex->current)) {
            printf("*");
            counter++;
        } 
        //Print out the 'empty' information
        printf(
            "\n"
            "Height: --\n"
            "Weight: --\n"
            "Type: --\n"
        );
        
        //Print all details if pokemon is found
    } else if (pokedex->current->found == FOUND) {
        printf("ID: ");
            print_pokemonid(pokedex->current);
            printf(
            "\nName: %s\n"
            "Height: %.1fm\n"
            "Weight: %.1fkg\n"
            "Type: ",
            pokemon_name(pokedex->current->pokemon), 
            pokemon_height(pokedex->current->pokemon), 
            pokemon_weight(pokedex->current->pokemon)
        );
        //Only write the types for pokemon if they are not none
        if (pokemon_first_type(pokedex->current->pokemon) == NONE_TYPE) {
        } else {
            printf("%s ", pokemon_type_to_string(pokemon_first_type(pokedex->current->pokemon)));
        }
        if (pokemon_second_type(pokedex->current->pokemon) == NONE_TYPE) {
        } else {
            printf("%s", pokemon_type_to_string(pokemon_second_type(pokedex->current->pokemon)));
        }
        printf("\n");
    }
}

// Return the currently selected Pokemon.
Pokemon get_current_pokemon(Pokedex pokedex) {
    
    //If no pokemon have been added - exit program
    no_pokemon(pokedex);
    
    return pokedex->current->pokemon;
}

// Sets the currently selected Pokemon to be 'found'.
void find_current_pokemon(Pokedex pokedex) {
    //If pokedex is empty
    if (pokedex->current == NULL) {
    
    //Set current pokemon to found
    } else {
        pokedex->current->found = FOUND;
    }
}

// Print out all of the Pokemon in the Pokedex.
void print_pokemon(Pokedex pokedex) {

    //If pokedex is empty
    if (pokedex->current == NULL) {
        //Do nothing
        
        //Print out pokedex as a list of information
    } else {
        struct pokenode *scroll = pokedex->head;
        
        //Go though pokedex
        while (scroll != NULL) {
        
            //Print arrow for current pokemon
            if (scroll == pokedex->current) {
                printf("--> ");
            } else {
                printf("    ");
            }
            //Print ID in proper format
            printf("#");
            print_pokemonid(scroll);
            printf(": ");
            
            //Only if pokemon is found - write pokemon name
            if (scroll->found == FOUND) {
                printf("%s", pokemon_name(scroll->pokemon));
                
            } else {
                //Print out the same number of asterisks as the pokemon name
               int counter = 0;
               while (counter < string_length(scroll)) {
                   printf("*");
                   counter++;
               }
            }
            printf("\n");
            scroll= scroll->next;
        }
    }
}

//////////////////////////////////////////////////////////////////////
//                         Stage 2 Functions                        //
//////////////////////////////////////////////////////////////////////

// Change the currently selected Pokemon to be the next Pokemon in the Pokedex.
void next_pokemon(Pokedex pokedex) {
    //No pokemon
    if (pokedex->current == NULL) {
        //Do nothing
        
        //Current is at end of list
    } else if (pokedex->current->next == NULL) {
        //Do nothing
        
    } else {
        //Move current pokemon one forward in list
        pokedex->current = pokedex->current->next;
    }
}

// Change the currently selected Pokemon to be the previous Pokemon in the Pokedex.
void prev_pokemon(Pokedex pokedex) {
    
    //Make a pointer to scroll through pokedex and previous to lag one behind
    struct pokenode *scroll = pokedex->head;
    struct pokenode *previous = NULL;
    
    if (pokedex->current == NULL) {
        //Do nothing
    } else if (pokedex->current == pokedex->head) {
        //Do nothing
    } else {
        //Continue through list until scroll pointing at the currently selected
        // pokemon
        while (scroll != pokedex->current) {
            previous = scroll;
            scroll = scroll->next;
        }
        //Move current pokemon one back in the list
        pokedex->current = previous;
    }
}

// Change the currently selected Pokemon to be the Pokemon with the ID `id`.
void change_current_pokemon(Pokedex pokedex, int id) {
    
    //Make a pointer to scroll through pokedex
    struct pokenode *scroll = pokedex->head;
    
    //Scroll through pokedex until pokemon with 'id' is found
    while (scroll != NULL && id != pokemon_id(scroll->pokemon)) {
        scroll = scroll->next;
    }
    //Set current pokemon to pokemon with this ID
    if (scroll != NULL) {
        pokedex->current = scroll;
    }
    
}

// Remove the currently selected Pokemon from the Pokedex.
void remove_pokemon(Pokedex pokedex) {
    //When pokedex is empty
    if (pokedex->current == NULL) {
        //Do nothing
        
    } else {
    
        //Create 2 pointers - Scroll equals to current pokemon and Previous lags
        // one behind
        struct pokenode *scroll = pokedex->head;
        struct pokenode *previous = NULL;
        
        //Set scroll to current and previous to one node before
        while (scroll != pokedex->current) {
            previous = scroll;
            scroll = scroll->next;
        }
        
        //Reattach pokedex around the current pokemon
        //If current is the head of the pokedex
        if (scroll == pokedex->head) {
            //Check whether there's anything after the first node
            if (pokedex->head->next == NULL) {
                pokedex->current = NULL;
                pokedex->head = NULL;
            } else {
            pokedex->head = scroll->next;
            pokedex->current = pokedex->head;
            }
            
            //If current is the last node in pokedex
        } else if (scroll->next == NULL) {
            previous->next = NULL;
            pokedex->current = previous;
            
            //Every node in between
        } else {
            previous->next = scroll->next;
            pokedex->current = previous->next;
        }
        //Delete the current node
        destroy_pokemon(scroll->pokemon);
        free(scroll);
    }
}

// Destroy the given Pokedex and free all associated memory.
void destroy_pokedex(Pokedex pokedex) {
    //If Pokedex is empty
    if (pokedex->head == NULL) {
        //Do nothing
        
        //If only one pokemon to free
    } else if (pokedex->head->next == NULL) {
        destroy_pokemon(pokedex->head->pokemon);
        free(pokedex->head);
        
        //If more than 1 pokemon in pokedex
    } else {
        struct pokenode *scroll = pokedex->head;
        struct pokenode *previous = NULL;
        
        while (scroll != NULL) {
            previous = scroll;
            scroll = scroll->next;
            destroy_pokemon(previous->pokemon);
            free(previous);
        }
    }
    free(pokedex);
}

//////////////////////////////////////////////////////////////////////
//                         Stage 3 Functions                        //
//////////////////////////////////////////////////////////////////////

// Print out all of the different types of Pokemon in the Pokedex.
void show_types(Pokedex pokedex) {
    
    struct pokenode *scroll = pokedex->head;
    int type[MAX_TYPE] = {0};
    //Never print None_Type
    type[0] = 1;
    
    //Go thorugh each pokemon in pokedex
    //Print all unique types from pokemon
    while (scroll != NULL) {
        
        //Check Type 1
        int check = pokemon_first_type(scroll->pokemon);
        //Check if pokemon type has already been printed
        if (type[check] == 1) {
            //Do nothing
        } else {
            printf("%s\n", 
            pokemon_type_to_string(pokemon_first_type(scroll->pokemon)));
            type[check] = 1;
        }
        
        //Check Type 2
        check = pokemon_second_type(scroll->pokemon);
        //Check if pokemon type has already been printed
        if (type[check] == 1) {
            //Do nothing
        } else {
            printf("%s\n", 
            pokemon_type_to_string(pokemon_second_type(scroll->pokemon)));
            type[check] = 1;
        }
        scroll = scroll->next;
    }
}


// Set the first not-yet-found Pokemon of each type to be found.
void go_exploring(Pokedex pokedex) {
    
    //Create a crolling pointer for pokedex and an array to hold types
    struct pokenode *scroll = pokedex->head;
    int type[MAX_TYPE] = {0};
    //Set None_type to found so it won't trigger 
    type[0] = 1;
    
    //Scroll through the pokedex
    while (scroll != NULL) {
    
        //If pokemon is found - disregard
        if (scroll->found == FOUND) {
            
            //If Hidden check type
            //Make every pokemon with a new type found 
        } else {
        
            //Check Type 1
        int check = pokemon_first_type(scroll->pokemon);
        //Check if pokemon type has already been found - Do nothing
        if (type[check] == 1) {
            //Else set pokemon to found and store type
        } else {
            scroll->found = FOUND;
            type[check] = 1;
        }
        
        //Check Type 2
        check = pokemon_second_type(scroll->pokemon);
        //Check if pokemon type has already been found- Do nothing
        if (type[check] == 1) {
            //Else set pokemon to found and store type
        } else {
            scroll->found = FOUND;
            type[check] = 1;
        }
        }
        scroll = scroll->next;
    }
}

// Return the total number of Pokemon in the Pokedex.
int count_total_pokemon(Pokedex pokedex) {
    
    struct pokenode *scroll = pokedex->head;
    int poke_counter = 0;
    //Count the number of pokemon by scrolling through the pokedex
    while (scroll != NULL) {
        poke_counter++;
        scroll = scroll->next;
    }
    return poke_counter;
}

// Return the number of Pokemon in the Pokedex that have been found.
int count_found_pokemon(Pokedex pokedex) {
    
    struct pokenode *scroll = pokedex->head;
    int poke_counter = 0;
    //Count the number of pokemon by scrolling through the pokedex
    while (scroll != NULL) {
        if (scroll->found == FOUND) {
            poke_counter++;
        }
        scroll = scroll->next;
    }
    return poke_counter;
    
}

//////////////////////////////////////////////////////////////////////
//                         Stage 4 Functions                        //
//////////////////////////////////////////////////////////////////////

// Add the information that the Pokemon with the ID `from_id` can
// evolve into the Pokemon with the ID `to_id`.
void add_pokemon_evolution(Pokedex pokedex, int from_id, int to_id) {

        //Make a pointer to scroll through pokedex + point at 
        //pokemon and its evolution
        struct pokenode *scroll = pokedex->head;
        struct pokenode *base = NULL;
        struct pokenode *evolution = NULL;
        
        //Scroll through pokedex until pokemon with 'from_id' is found
        //For base pokemon
        while (scroll != NULL && from_id != pokemon_id(scroll->pokemon)) {
            scroll = scroll->next;
        }
        //Set base pointer to the pokemon with this ID
        if (scroll != NULL) {
            base = scroll;
            
            //ID doesn't exist
        } else {
            printf("At this point in time, no pokemon has the base ID: %d and "
               "this command cannot function\n", from_id);
               exit(1);
        }
        
        //Repeat method for evoultion of base pokemon
        //Scroll through pokedex until pokemon with 'to_id' is found
        //For evolution pokemon
        scroll = pokedex->head;
        while (scroll != NULL && to_id != pokemon_id(scroll->pokemon)) {
            scroll = scroll->next;
        }
        //Set evolution pointer to the pokemon with this ID
        if (scroll != NULL) {
            evolution = scroll;
            
            //ID doesn't exist
        } else {
            printf("At this point in time, no pokemon has the evolution ID: %d "
               "and this command cannot function\n", to_id);
               exit(1);
        }
        
        //Connect the base pokemon to the evolution pokemon_first_type
        //Unless id's are equal
        if (to_id == from_id) {
            printf("Pokemon with the same ID: %d cannot be an evolution of "
                   "itself\n", to_id);
        } else {
            base->evolution = evolution;
        }
}

// Show the evolutions of the currently selected Pokemon.
void show_evolutions(Pokedex pokedex) {
    
    struct pokenode *evolve = pokedex->current;
    if (pokedex->current == NULL) {
    
    } else {
    
        //Print out current pokemon
        //When hidden
        if (evolve->found == HIDDEN) {
            printf("#");
            print_pokemonid(evolve);
            printf(" ???? [????]");
          
          //When found  
        } else {
            printf("#");
            print_pokemonid(evolve);
            printf(" %s ", pokemon_name(pokedex->current->pokemon));
            
            //Only write the types for pokemon if they are not none
            printf("[");
            if (pokemon_first_type(evolve->pokemon) == NONE_TYPE) {
            } else {
                printf("%s",
                pokemon_type_to_string(pokemon_first_type(evolve->pokemon)));
            }
            if (pokemon_second_type(evolve->pokemon) == NONE_TYPE) {
            } else {
                printf(", %s",
                pokemon_type_to_string(pokemon_second_type(evolve->pokemon)));
            }
            printf("]");
        }
        
        //Start printing out evolutions
        evolve = evolve->evolution;
        while (evolve != NULL) {
            
            printf(" --> ");
            
            //When Hidden
            if (evolve->found == HIDDEN) {
            printf("#");
            print_pokemonid(evolve);
            printf(" ???? [????]");
          
          //When found  
        } else {
            printf("#");
            print_pokemonid(evolve);
            printf(" %s ", pokemon_name(evolve->pokemon));
            
            //Only write the types for pokemon if they are not none
            printf("[");
            if (pokemon_first_type(evolve->pokemon) == NONE_TYPE) {
            } else {
                printf("%s",
                pokemon_type_to_string(pokemon_first_type(evolve->pokemon)));
            }
            if (pokemon_second_type(evolve->pokemon) == NONE_TYPE) {
            } else {
                printf(", %s",
                pokemon_type_to_string(pokemon_second_type(evolve->pokemon)));
            }
            printf("]");
        }
            evolve = evolve->evolution;
        }
            
        printf("\n");
    }
    
}

// Return the pokemon_id of the Pokemon that the currently selected
// Pokemon evolves into.
int get_next_evolution(Pokedex pokedex) {

    //Empty Pokedex
    if (pokedex->current == NULL) {
        printf("At this point in time, no pokemon have been added and this "
               "command cannot function\n");
        exit(1);
        
        //Return id for the evolution of current pokemon
    } else {
        if (pokedex->current->evolution == NULL) {
            return DOES_NOT_EVOLVE;
        } else {
            return pokemon_id(pokedex->current->evolution->pokemon);
        }
    }
}

//////////////////////////////////////////////////////////////////////
//                         Stage 5 Functions                        //
//////////////////////////////////////////////////////////////////////

// Create a new Pokedex which contains only the Pokemon of a specified
// type from the original Pokedex.
Pokedex get_pokemon_of_type(Pokedex pokedex, pokemon_type type) {
    fprintf(stderr, "exiting because you have not implemented the get_pokemon_of_type function\n");
    exit(1);
}

// Create a new Pokedex which contains only the Pokemon that have
// previously been 'found' from the original Pokedex.
Pokedex get_found_pokemon(Pokedex pokedex) {
    fprintf(stderr, "exiting because you have not implemented the get_found_pokemon function\n");
    exit(1);
}

// Create a new Pokedex containing only the Pokemon from the original
// Pokedex which have the given string appearing in its name.
Pokedex search_pokemon(Pokedex pokedex, char *text) {
    fprintf(stderr, "exiting because you have not implemented the search_pokemon function\n");
    exit(1);
}

// Add definitions for your own functions here.
// Make them static to limit their scope to this file.
//
// My Functions
//

//Outputs string length
int string_length(struct pokenode *string) {
    
    //Go through string and count each letter
    int counter = 0;
    char *name = pokemon_name(string->pokemon);   
    while (name[counter] != '\0') {
        counter++;
    }
    
    return counter;
}

//Print out the pokemon ID in valid format
void print_pokemonid(struct pokenode *print) {
    
    //Print out 2 extra 0's if < 10 and 1 extra 0 if > 9 and < 100
    if (pokemon_id(print->pokemon) < 10) {
        printf("00%d", pokemon_id(print->pokemon));
    } else if (pokemon_id(print->pokemon) > 9 &&
               pokemon_id(print->pokemon) < 100) {
       printf("0%d", pokemon_id(print->pokemon)); 
    } else {
        printf("%d", pokemon_id(print->pokemon));
    }
} 

//Will print out error message and exit program if no pokemon are inputted
void no_pokemon(Pokedex pokedex) {
    //If no pokemon have been added 
    if (pokedex->current == NULL) {
        printf("At this point in time, no pokemon have been added and this "
               "command cannot function\n");
               exit(1);
   }
}
