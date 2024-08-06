#include "word_ladder.h"

#include <catch2/catch.hpp>

// ============== Test Cases - read_lexicon() ==================
TEST_CASE("word_ladder::read_lexicon works as expected") {
	auto const lexicon = word_ladder::read_lexicon("english.txt");

    CHECK(lexicon.empty() == false);
}

TEST_CASE("read_lexicon: Read in the provided english.txt") {
    auto const lexicon = word_ladder::read_lexicon("english.txt");

    REQUIRE(lexicon.empty() == false);
    CHECK(lexicon.find("cat") != lexicon.end());
    CHECK(lexicon.find("abandonments") != lexicon.end());
    CHECK(lexicon.find("petrol") != lexicon.end());

}

TEST_CASE("read_lexicon: Invalid Path") {
    auto const lexicon = word_ladder::read_lexicon("fake.txt");

    CHECK(lexicon.empty() == true);
}


TEST_CASE("generate: at -> it") {
	auto const lexicon = std::unordered_set<std::string>{
		"at",
		"it"
	};

	const auto expected = std::vector<std::vector<std::string>>{
		{"at", "it"}
	};

	auto const ladders = word_ladder::generate("at", "it", lexicon);

	CHECK(ladders == expected);
}


// ============== Test Cases - generate() ==================
TEST_CASE("generate: Simple 1 Letter Change (gadi -> gads)") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "gadi", "gads", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { { "gadi", "gads"} };
    CHECK(result == expected);
}

TEST_CASE("generate: Simple Test cat -> bat (one letter change)") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "cat", "bat", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { { "cat", "bat"} };
    CHECK(result == expected);
}

TEST_CASE("generate: work -> play (Multiple Paths + Alphabetical Order)") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "work", "play", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { { "work", "fork", "form", "foam", "flam", "flay", "play" }, 
                                                                  { "work", "pork", "perk", "peak", "pean", "plan", "play" }, 
                                                                  { "work", "pork", "perk", "peak", "peat", "plat", "play" }, 
                                                                  { "work", "pork", "perk", "pert", "peat", "plat", "play" }, 
                                                                  { "work", "pork", "porn", "pirn", "pian", "plan", "play" },
                                                                  { "work", "pork", "port", "pert", "peat", "plat", "play" }, 
                                                                  { "work", "word", "wood", "pood", "plod", "ploy", "play" }, 
                                                                  { "work", "worm", "form", "foam", "flam", "flay", "play" }, 
                                                                  { "work", "worn", "porn", "pirn", "pian", "plan", "play" }, 
                                                                  { "work", "wort", "bort", "boat", "blat", "plat", "play" }, 
                                                                  { "work", "wort", "port", "pert", "peat", "plat", "play" }, 
                                                                  { "work", "wort", "wert", "pert", "peat", "plat", "play" } };
    CHECK(result == expected);
}


TEST_CASE("generate: awake -> sleep (Multiple Shortest paths)") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "awake", "sleep", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { {"awake","aware","sware","share","sharn","shawn","shewn","sheen","sheep","sleep"},
                                                                  {"awake","aware","sware","share","shire","shirr","shier","sheer","sheep","sleep"} };
    CHECK(result == expected);
}

TEST_CASE("generate: code -> data") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "code", "data", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { { "code", "cade", "cate", "date", "data" },
                                                                  { "code", "cote", "cate", "date", "data" },
                                                                  { "code", "cote", "dote", "date", "data" } };
    CHECK(result == expected);
}

TEST_CASE("generate: airplane -> tricycle (No Word Ladder)") {
    const auto lexicon = word_ladder::read_lexicon("english.txt");
    REQUIRE(lexicon.empty() == false);

    const auto result = word_ladder::generate( "airplance", "tricycle", lexicon);
    const auto expected = std::vector<std::vector<std::string>> { };
    CHECK(result == expected);
}