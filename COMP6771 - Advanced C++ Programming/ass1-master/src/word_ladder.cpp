#include "word_ladder.h"
#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <unordered_set>
#include <queue>
#include <set>
#include <algorithm>

/*
    Parameters:
        path - a string used to access the file containing words
    Return Value:
        unordered_set<string> - The lexicon to use in generate
    Functionality:
        - Opens a stream to the file defined by the 'path parameter'
        - Takes input from file delineated by '\n' and inserts it into an unordered set
*/
auto word_ladder::read_lexicon(const std::string &path) -> std::unordered_set<std::string> {
    // Open File Stream
    std::ifstream word_file(path); 
    std::unordered_set<std::string> dictionary; 

    // Add each word separated by a newline to the dictionary variable
    if (word_file.is_open()) {
        std::string curr_word;
        while (std::getline(word_file, curr_word)) {
            dictionary.insert(curr_word);
        }
    }

	return dictionary;
}

/*
    Parameters:
        from - start word to begin transformation
        to  - end word to finish transformation
        lexicon - valid words for intermediate transformations
    Return Value:
        vector<vector<string>> - Contains the shortest path/s for (from -> to) 
                               - Empty if no paths exist
    Functionality:
        1. Go through each letter in the current word (E.g. cat -> c, a, t)
        2. Check if changing the current letter to any letter in the alphabet results in a new word (E.g cat -> aat, bat, cat, dat, eat)
        3. Check that the word isn't in the container of already used words (At start only the 'from' word e.g. cat) + Check if the new word is in the lexicon
        4. If the conditions are passed - Add the new word to a queue (E.g. {cat, bat}, {cat, eat}, {cat, fat}  
        5. Add the current word to the container with used words (Only after every queue has been checked)
        7. Once a path is found that works - complete any remaining queues but do not progress onto any others as these are the shortest possible paths
        8. Copy the completed paths into an appropriate return type variable
*/
auto word_ladder::generate(
	const std::string &from,
	const std::string &to,
	const std::unordered_set<std::string> &lexicon
) -> std::vector<std::vector<std::string>> {

    // =================== Initialisation Section =====================
    // Initialise the vector of queues with one queue containing 'from' E.g.< {"cat"} >
    std::vector<std::queue<std::string>> old_list;
    std::queue<std::string> initial_q;
    initial_q.push(from);
    old_list.push_back(initial_q);

    // Initialise the used_words set with 'from' to prevent paths containing cycles
    std::unordered_set<std::string> used_words;
    used_words.insert(from);

    // Initialise Variables
    //     alphabet     - For future transformations
    //     new_list     - To temporarily store new valid paths
    //     return_value - Holds the shortest path/s once found 
    auto alphabet = std::string {"abcdefghijklmnopqrstuvwxyz"};
    std::vector<std::queue<std::string>> new_list;
    std::vector<std::vector<std::string>> return_value;

    // Continue hopping between valid words until:
    // A shortest path is found OR No valid words hops can be made 
    while (old_list.empty() != true and return_value.size() == 0) {

        // Iterate over each queue in old_list vector
        new_list.clear();
        auto temp_used_words = std::set<std::string> {};
        for (auto curr_q : old_list) {
            std::string original_word = curr_q.back();
            for (std::string::size_type position = 0; position != original_word.size(); position++) {
                //std::cout << "Current Letter: " << original_word[position] << "\n";
                std::string current_word = original_word;
                for (char letter : alphabet) {
                    current_word[position] = letter;
                    if (not used_words.contains(current_word) and lexicon.contains(current_word)) {
                        //std::cout << current_word << " - Check\n";
                        std::queue<std::string> temp_queue = curr_q;
                        temp_queue.push(current_word);
                        new_list.push_back(temp_queue);
                        temp_used_words.insert(current_word);
                        if (current_word == to) {
                            //std::cout << current_word << " found!!\n";
                            return_value.push_back(queue_to_vector(temp_queue));
                        }
                    }
                }
            }
        }
        //print_new_list(new_list, to);
        for (auto iter = temp_used_words.begin(); iter != temp_used_words.end(); ++iter) {
            used_words.insert(*iter);
        }
        old_list = new_list;
    }
    std::sort(return_value.begin(), return_value.end());
	return return_value;
}

/*
    Parameters:
        queue - a queue containg strings
    Return Value:
        vector<string>
    Functionality:
        - Take each element out of the queue starting at the front and insert it into the return vector
*/
std::vector<std::string> word_ladder::queue_to_vector(std::queue<std::string>& queue) {
    std::vector<std::string> new_vector;
    while (queue.empty() == false) {
        new_vector.push_back(queue.front());
        queue.pop();
    }
    return new_vector;
}

/*
    Parameters:
        list - vector of queues
        to - target word
    Return Value:
        void
    Functionality:
        1. Print out each element of multiple queues stored within a vector
*/
void word_ladder::print_new_list(std::vector<std::queue<std::string>> list, std::string to) {
    std::cout << "NEW LIST-------------------------\n";
    for (auto queue : list) {
        std::cout << "{ ";        
        while (queue.empty() == false) {
            if (queue.front() == to) {
                std::cout << "HERE!!";
            }
        std::cout << " " << queue.front() << " ";
        queue.pop();
        }
        std::cout << " }\n";        
    }
}