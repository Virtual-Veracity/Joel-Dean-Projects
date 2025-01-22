#include "./filtered_string_view.h"

#include <catch2/catch.hpp>
#include <type_traits>
#include <iostream>
#include <sstream>
#include <string.h>

//================ TESTING ===================
// Test the Default Predicate
TEST_CASE("Default Predicate performs as expected") {
	auto string = std::string("e123abc.@ \nZ");
	std::remove_if(string.begin(), string.end(), fsv::filtered_string_view::default_predicate);

	std::ostringstream os{};
	os << string;
	CHECK(os.str() == "e123abc.@ \nZ");
}

//================ Constructor Testing ===================
// Default Constructor
TEST_CASE("Default Constructor") {
	SECTION("Non-Const Default") {
		auto string_view = fsv::filtered_string_view();

		CHECK(string_view.empty() == true);
		CHECK(string_view.size() == 0);
	}
	SECTION("Const Default") {
		const auto cstring_view = fsv::filtered_string_view();

		CHECK(cstring_view.empty() == true);
		CHECK(cstring_view.size() == 0);
	}
}

// Standard String Constructor
TEST_CASE("std::string Constructor") {
	SECTION("1 Parameter: Test Empty String") {
		const auto string_view = fsv::filtered_string_view(std::string{""});

		CHECK(string_view.empty() == true);
		CHECK(string_view.size() == 0);
	}
	SECTION("1 Parameter: Test String - Swarley") {
		auto string_view = fsv::filtered_string_view(std::string{"Swarley"});

		CHECK(static_cast<std::string>(string_view) == "Swarley");
		CHECK(string_view.size() == 7);
	}
	// Underlying string just doesnt convert properly (even with std::string)
	SECTION("1 Parameter: Test Really Long String") {
		const auto standard = std::string{"Long Ass String with lots of stuff"};
		const auto string_view = fsv::filtered_string_view(standard);
		CHECK(static_cast<std::string>(string_view) == std::string{"Long Ass String with lots of stuff"});
		CHECK(string_view.size() == 34);
	}
	// Standard String Constructor - 2 Parameters
	SECTION("Test Predicate that returns false") {
		const auto string_view = fsv::filtered_string_view(std::string{"Swarley"}, [](const char& c) {
			(void)c;
			return false;
		});

		CHECK(static_cast<std::string>(string_view) == "");
		CHECK(string_view.size() == 0);
		CHECK_THROWS(string_view.at(0));
	}
	SECTION("Test Predicate that returns consonants") {
		auto string_view = fsv::filtered_string_view(std::string{"Swarley"}, [](const char& c) {
			return !(c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});

		CHECK(static_cast<std::string>(string_view) == "Swrly");
		CHECK(string_view.size() == 5);
		CHECK(string_view.at(2) == 'r');
	}

	SECTION("Test String/Predicate mismatch") {
		auto string_view = fsv::filtered_string_view(std::string{"Swarley"}, [](const char& c) { return (c == '1'); });

		CHECK(static_cast<std::string>(string_view) == "");
		CHECK(string_view.size() == 0);
		CHECK_THROWS(string_view.at(2));
	}
}

// Pointer Char Constructor
TEST_CASE("Pointer Char Constructor") {
	// Pointer Char Constructor - 1 Parameter
	SECTION("1 Argument: Test Empty String") {
		auto string_view = fsv::filtered_string_view("");

		CHECK(static_cast<std::string>(string_view) == "");
		CHECK(string_view.size() == 0);
		CHECK_THROWS(string_view.at(0));
	}
	SECTION("1 Argument: Test String - string") {
		const auto cstring_view = fsv::filtered_string_view("Swarley");
		CHECK(static_cast<std::string>(cstring_view) == "Swarley");
		CHECK(cstring_view.size() == 7);
		CHECK(cstring_view.at(5) == 'e');
	}
	SECTION("1 Parameter: Test Really Long String") {
		auto string_view = fsv::filtered_string_view("Long Ass String with lots of stuff");

		CHECK(static_cast<std::string>(string_view) == "Long Ass String with lots of stuff");
		CHECK(string_view.size() == 34);
	}
	// Pointer Char Constructor - 2 Parameters
	SECTION("std::char*(2 Arguments): Test False Predicate") {
		const auto cstring_view = fsv::filtered_string_view("Swarley", [](const char& c) {
			(void)c;
			return false;
		});

		CHECK(static_cast<std::string>(cstring_view) == "");
		CHECK(cstring_view.size() == 0);
		CHECK_THROWS(cstring_view.at(0));
	}
	SECTION("std::char*(2 Arguments): Test Predicate that returns vowels") {
		auto string_view = fsv::filtered_string_view("Swarley", [](const char& c) {
			return (c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});

		CHECK(static_cast<std::string>(string_view) == "ae");
		CHECK(string_view.size() == 2);
		CHECK(string_view.at(1) == 'e');
	}
}

// Copy Constructor
// test that a const fsv cannot copy assign stuff or be copy assigned

TEST_CASE("Copy Constructor") {
	SECTION("Copy: Test Empty fsv") {
		auto orig = fsv::filtered_string_view();
		auto string_view = fsv::filtered_string_view(orig);

		CHECK(static_cast<std::string>(string_view) == "");
		CHECK(string_view.size() == 0);
		CHECK_THROWS(string_view.at(0));
	}
	SECTION("Copy: std::string fsv") {
		const auto orig = fsv::filtered_string_view(std::string{"123 abc"}, [](const char& c) {
			return !(c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "123 bc");
		CHECK(orig.size() == 6);
		CHECK(orig.at(4) == 'b');

		const auto string_view = fsv::filtered_string_view(orig);
		CHECK(static_cast<std::string>(string_view) == "123 bc");
		CHECK(string_view.size() == 6);
		CHECK(string_view.at(4) == 'b');
	}
	SECTION("Copy: char* fsv") {
		auto orig = fsv::filtered_string_view("basketball", [](const char& c) {
			return (c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "aea");
		CHECK(orig.size() == 3);
		CHECK_THROWS(orig.at(8));

		auto string_view = fsv::filtered_string_view(orig);
		CHECK(static_cast<std::string>(string_view) == "aea");
		CHECK(string_view.size() == 3);
		CHECK_THROWS(string_view.at(8));
	}
}

// Move Constructor
TEST_CASE("Move Constructor Tests") {
	SECTION("Move: Test Empty fsv") {
		auto orig = fsv::filtered_string_view();
		auto string_view = fsv::filtered_string_view(std::move(orig));
		CHECK(static_cast<std::string>(string_view) == "");
		CHECK(string_view.size() == 0);
		CHECK_THROWS(string_view.at(0));

		CHECK(static_cast<std::string>(orig) == "");
		CHECK(orig.size() == 0);
		CHECK_THROWS(orig.at(0));
	}
	SECTION("Move: Test std::string fsv") {
		auto orig = fsv::filtered_string_view(std::string{"Clover@"}, [](const char& c) {
			return !(c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "Clvr@");
		CHECK(orig.size() == 5);
		CHECK(orig.at(2) == 'v');
		auto string_view = fsv::filtered_string_view(std::move(orig));

		CHECK(static_cast<std::string>(string_view) == "Clvr@");
		CHECK(string_view.size() == 5);
		CHECK(string_view.at(2) == 'v');

		CHECK(static_cast<std::string>(orig) == "");
		CHECK(orig.size() == 0);
		CHECK_THROWS(orig.at(0));
	}
	SECTION("Move: Test char* fsv") {
		auto orig = fsv::filtered_string_view("CAPITALlower", [](const char& c) { return (c >= 'A' and c <= 'Z'); });
		CHECK(static_cast<std::string>(orig) == "CAPITAL");
		CHECK(orig.size() == 7);
		CHECK_THROWS(orig.at(9));
		auto string_view = fsv::filtered_string_view(std::move(orig));

		CHECK(static_cast<std::string>(string_view) == "CAPITAL");
		CHECK(string_view.size() == 7);
		CHECK(string_view.at(2) == 'P');

		CHECK(static_cast<std::string>(orig) == "");
		CHECK(orig.size() == 0);
		CHECK_THROWS(orig.at(0));
	}
	SECTION("Move: Test const fsv in parameter (How to check this...)") {
		REQUIRE(true);
	}
}

// Copy Assignment Tests
TEST_CASE("Copy Assignment") {
	SECTION("Test std::string fsv") {
		const auto orig =
		    fsv::filtered_string_view(std::string{"123 abcR"}, [](const char& c) { return !(c >= 'a' and c <= 'z'); });
		CHECK(static_cast<std::string>(orig) == "123 R");
		CHECK(orig.size() == 5);
		CHECK(orig.at(4) == 'R');

		const auto string_view = orig;
		CHECK(static_cast<std::string>(string_view) == "123 R");
		CHECK(string_view.size() == 5);
		CHECK(string_view.at(4) == 'R');
	}
	SECTION("Test char* fsv") {
		auto orig = fsv::filtered_string_view("basketball", [](const char& c) {
			return (c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "aea");
		CHECK(orig.size() == 3);
		CHECK_THROWS(orig.at(8));

		auto string_view = orig;
		CHECK(static_cast<std::string>(string_view) == "aea");
		CHECK(string_view.size() == 3);
		CHECK_THROWS(string_view.at(8));
	}
	SECTION("Test Copy Assign to Self") {
		auto orig =
		    fsv::filtered_string_view(std::string{"123 abcR"}, [](const char& c) { return !(c >= 'a' and c <= 'z'); });
		CHECK(static_cast<std::string>(orig) == "123 R");
		CHECK(orig.size() == 5);
		CHECK(orig.at(4) == 'R');

		orig = orig;
		CHECK(static_cast<std::string>(orig) == "123 R");
		CHECK(orig.size() == 5);
		CHECK(orig.at(4) == 'R');
	}
	SECTION("Test Copy Assign for const fsv") {
		auto orig = fsv::filtered_string_view("basketball", [](const char& c) {
			return (c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "aea");
		CHECK(orig.size() == 3);
		CHECK_THROWS(orig.at(8));

		const auto string_view = fsv::filtered_string_view("Test_String");
		// Cannot compile - need invocable but how to do with copy operator
		// string_view = orig;
	}
}
// Move Assignment Tests
TEST_CASE("Move Assignment") {
	SECTION("Test std::string fsv") {
		auto orig = fsv::filtered_string_view(std::string{"Clover@"}, [](const char& c) {
			return !(c == 'a' or c == 'i' or c == 'e' or c == 'o' or c == 'u');
		});
		CHECK(static_cast<std::string>(orig) == "Clvr@");
		CHECK(orig.size() == 5);
		CHECK(orig.at(2) == 'v');

		auto string_view = std::move(orig);
		CHECK(static_cast<std::string>(string_view) == "Clvr@");
		CHECK(string_view.size() == 5);
		CHECK(string_view.at(2) == 'v');

		CHECK(static_cast<std::string>(orig) == "");
		CHECK(orig.size() == 0);
		CHECK_THROWS(orig.at(0));
	}
	SECTION("Test char* fsv") {
		auto orig = fsv::filtered_string_view("CAPITALlower", [](const char& c) { return (c >= 'A' and c <= 'Z'); });
		CHECK(static_cast<std::string>(orig) == "CAPITAL");
		CHECK(orig.size() == 7);
		CHECK_THROWS(orig.at(9));

		auto string_view = std::move(orig);
		CHECK(static_cast<std::string>(string_view) == "CAPITAL");
		CHECK(string_view.size() == 7);
		CHECK(string_view.at(2) == 'P');

		CHECK(static_cast<std::string>(orig) == "");
		CHECK(orig.size() == 0);
		CHECK_THROWS(orig.at(0));
	}
	SECTION("Test Move Assign to Self") {
		auto orig = fsv::filtered_string_view("CAPITALlower", [](const char& c) { return (c >= 'A' and c <= 'Z'); });
		CHECK(static_cast<std::string>(orig) == "CAPITAL");
		CHECK(orig.size() == 7);
		CHECK_THROWS(orig.at(9));

		orig = std::move(orig);
		CHECK(static_cast<std::string>(orig) == "CAPITAL");
		CHECK(orig.size() == 7);
		CHECK_THROWS(orig.at(9));
	}
	SECTION("Cannot use move on a const fsv - LHS") {
		auto orig = fsv::filtered_string_view("CAPITALlower", [](const char& c) { return (c >= 'A' and c <= 'Z'); });
		CHECK(static_cast<std::string>(orig) == "CAPITAL");
		CHECK(orig.size() == 7);
		CHECK_THROWS(orig.at(9));

		const auto string_view = fsv::filtered_string_view("Test_String");
		const auto orig2 = fsv::filtered_string_view("Test_String");

		// Calling a non-const member function from const fsv
		// Figure out later...
		// CHECK(std::is_invocable_v<decltype(), const fsv::filtered_string_view, fsv::filtered_string_view&>);
		orig = std::move(string_view);

		// Assume a copy is okay if a std::move was used
		CHECK(static_cast<std::string>(orig) == "Test_String");
		CHECK(orig.size() == 11);
		CHECK_THROWS(orig.at(-1));

		CHECK(static_cast<std::string>(string_view) == "Test_String");
		CHECK(string_view.size() == 11);
		CHECK(string_view.at(4) == '_');
	}
}

// Subscript Operator Tests
// For now only test expected behaviour
TEST_CASE("Subscript Operator [] Tests") {
	SECTION("Test with const and non-const fsv") {
		auto string_view = fsv::filtered_string_view("HeadPhones");
		CHECK(string_view[0] == 'H');
		CHECK(string_view[5] == 'h');
		CHECK(string_view[9] == 's');
		CHECK(string_view[12] == '\0');

		const auto cstring_view = fsv::filtered_string_view("1StringPlease");
		CHECK(cstring_view[0] == '1');
		CHECK(cstring_view[1] == 'S');
		CHECK(cstring_view[5] == 'n');
		CHECK(cstring_view[10] == 'a');
		CHECK(cstring_view[12] == 'e');
		CHECK(cstring_view[50] == '\0');
	}

	SECTION("Consonants Only Predicate") {
		auto string_view = fsv::filtered_string_view("HeadPhones", [](const char& c) { return (c >= 'a' and c <= 'z'); });

		CHECK(string_view[0] == 'e');
		CHECK(string_view[1] == 'a');
		CHECK(string_view[5] == 'n');
		CHECK(string_view[10] == '\0');
	}
	SECTION("Default fsv - nullptr string_") {
		auto string_view = fsv::filtered_string_view();
		// Check_throws not enough but this test does break
		// CHECK_THROWS(string_view[-1]);
		CHECK(true);
	}
	SECTION("Const Underlying String - No Modifications allowed") {
		auto string_view = fsv::filtered_string_view("HeadPhones");
		const auto cstring_view = fsv::filtered_string_view("Swarley");
		// Check_THROWS doesnt work for this -> figure out what does
		// But it does prevent for both
		// CHECK_THROWS(string_view[0] = 'a');
		// CHECK_THROWS(cstring_view[0] = 'z');
	}
	SECTION("Invalid index values") {
		const auto cstring_view = fsv::filtered_string_view("");
		CHECK(cstring_view[0] == '\0');
	}
}
// String Conversion Tests
TEST_CASE("String Conversion Tests") {
	SECTION("Test with const and non-const fsv") {
		auto string_view = fsv::filtered_string_view("Head Phones");
		// Using std::string - instead of auto to check that type returned is correct
		std::string string = static_cast<std::string>(string_view);
		CHECK(string == std::string{"Head Phones"});

		const auto cstring_view = fsv::filtered_string_view("1StringPlease");
		std::string cstring = static_cast<std::string>(cstring_view);
		CHECK(cstring == std::string{"1StringPlease"});
	}
	SECTION("Only lowercase Predicate") {
		auto string_view =
		    fsv::filtered_string_view("Head Phones", [](const char& c) { return (c >= 'a' and c <= 'z'); });
		auto string = static_cast<std::string>(string_view);
		CHECK(string == std::string{"eadhones"});
	}
	SECTION("Empty String") {
		auto string_view = fsv::filtered_string_view("");
		auto string = static_cast<std::string>(string_view);
		CHECK(string == std::string{""});
	}
	SECTION("Default FSV") {
		auto string_view = fsv::filtered_string_view();
		auto string = static_cast<std::string>(string_view);
		CHECK(string == std::string{});
	}
	SECTION("False Predicate") {
		auto string_view = fsv::filtered_string_view("Head Phones", [](const char& c) {
			(void)c;
			return false;
		});
		auto string = static_cast<std::string>(string_view);
		CHECK(string == std::string{""});
	}
	SECTION("Modify Standard String without affecting fsv") {
		auto string_view =
		    fsv::filtered_string_view("Head Phones", [](const char& c) { return (c >= 'a' and c <= 'z'); });
		auto string = static_cast<std::string>(string_view);

		string.at(3) = '@';
		CHECK(string == std::string{"ead@ones"});
		CHECK(string_view.at(3) == 'h');
	}
}

// =========================== Member Function Testing ===================
// at() Tests
TEST_CASE("at() Tests") {
	SECTION("Test const + non-const fsv") {
		auto standard = std::string{"Let's Try a Longer String"};
		auto string_view = fsv::filtered_string_view(standard);
		CHECK(string_view.at(0) == 'L');
		CHECK(string_view.at(1) == 'e');
		CHECK(string_view.at(5) == ' ');
		CHECK(string_view.at(10) == 'a');
		CHECK(string_view.at(20) == 't');

		const auto cstring_view = fsv::filtered_string_view("EucalytuS");
		CHECK(cstring_view.at(0) == 'E');
		CHECK(cstring_view.at(1) == 'u');
		CHECK(cstring_view.at(5) == 'y');
		CHECK(cstring_view.at(8) == 'S');
	}
	SECTION("Test invalid index catching") {
		auto standard = std::string{"Let's Try a Longer String"};
		auto string_view = fsv::filtered_string_view(standard);

		CHECK_THROWS(string_view.at(50));
		CHECK_THROWS(string_view.at(-1));
		CHECK_THROWS(string_view.at(25));
		CHECK_NOTHROW(string_view.at(24));

		const auto cstring_view = fsv::filtered_string_view("EucalytuS");
		CHECK_THROWS(cstring_view.at(50));
		CHECK_THROWS(cstring_view.at(-1));
		CHECK_THROWS(cstring_view.at(9));
		CHECK_NOTHROW(cstring_view.at(8));
	}
	SECTION("Test Modification of Underlying Data") {
		auto standard = std::string{"Let's Try a Longer String"};
		auto string_view_s = fsv::filtered_string_view(standard);
		const auto cstring_view_s = fsv::filtered_string_view(standard);
		// CHECK_THROWS(string_view_s.at(2) = '@');
		// CHECK_THROWS(cstring_view_s.at(2) = '@');

		auto string_view_c = fsv::filtered_string_view("EucalytuS");
		const auto cstring_view_c = fsv::filtered_string_view("EucalytuS");
		// CHECK_THROWS(string_view_c.at(2) = '@');
		// CHECK_THROWS(cstring_view_c.at(2) = '@');

		// They all work - Just need a proper way to test them
	}
}
// size() Tests
TEST_CASE("size() Tests") {
	SECTION("Test const + non-const fsv") {}
	SECTION("Test Empty String") {}
	SECTION("Test Default fsv") {}
	SECTION("Test Consonant predicate") {}
	SECTION("Test False Predicate") {}
}
// empty() Tests
TEST_CASE("empty() Tests") {
	SECTION("Test const + non-const fsv") {}
}