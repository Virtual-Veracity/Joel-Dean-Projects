#ifndef COMP6771_WORD_LADDER_H
#define COMP6771_WORD_LADDER_H

#include <string>
#include <unordered_set>
#include <vector>
#include <queue>

namespace word_ladder {
// Given a file path to a newline-separated list of words...
// Loads those words into an unordered set and returns it.
auto read_lexicon(const std::string& path) -> std::unordered_set<std::string>;

// Given a start word and destination word, returns all the shortest possible paths from the
// start word to the destination, where each word in an individual path is a valid word per the
// provided lexicon.
// Preconditions:
// - from.size() == to.size()
// - lexicon.contains(from)
// - lexicon.contains(to)
auto generate(
    const std::string& from,
    const std::string& to,
    const std::unordered_set<std::string>& lexicon) -> std::vector<std::vector<std::string>>;

auto queue_to_vector(std::queue<std::string>& queue) -> std::vector<std::string>;
auto print_new_list(std::vector<std::queue<std::string>> list, std::string to) -> void;
} // namespace word_ladder

#endif // COMP6771_WORD_LADDER_H
