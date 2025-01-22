#include "./filtered_string_view.h"

#include <algorithm>
#include <iostream>
#include <string>
#include <utility>

#include <string.h>

using view = fsv::filtered_string_view;

// =================== Constructors ==================
// Default Constructor
view::filtered_string_view()
: filtered_string_view(nullptr, 0, default_predicate) {}

// Standard String Constructor
view::filtered_string_view(const std::string& str, filter predicate)
: string_{str.c_str()}
, size_{str.size()}
, predicate_{predicate} {}
//: filtered_string_view(str.c_str(), str.size(), predicate) {}

// Pointer Char Constructor
view::filtered_string_view(const char* str, filter predicate)
: filtered_string_view(str, strlen(str), predicate) {}

// Copy Constructor
view::filtered_string_view(const filtered_string_view& orig)
: filtered_string_view(orig.string_, orig.size_, orig.predicate_) {}

// Move Constructor
view::filtered_string_view(filtered_string_view&& orig) noexcept
: string_{std::exchange(orig.string_, nullptr)}
, size_{std::exchange(orig.size_, 0)}
, predicate_{std::exchange(orig.predicate_, default_predicate)} {}

// Delegate Constructor - Private Constructor
view::filtered_string_view(const char* str, std::size_t sz, filter pred)
: string_{str}
, size_{sz}
, predicate_{pred} {}

// =================== Member Operators ==================
// Copy Assignment
auto view::operator=(const filtered_string_view& orig) -> filtered_string_view& {
	string_ = orig.string_;
	size_ = orig.size_;
	predicate_ = orig.predicate_;
	return *this;
}

// Move Assignment
auto view::operator=(filtered_string_view&& orig) noexcept -> filtered_string_view& {
	// If Self Move -> Leave Unchanged
	if (this == &orig) {
		return *this;
	}

	std::swap(string_, orig.string_);
	std::swap(size_, orig.size_);
	std::swap(predicate_, orig.predicate_);

	orig.string_ = nullptr;
	orig.size_ = 0;
	orig.predicate_ = default_predicate;
	return *this;
}

// Const Subscript Operator
auto view::operator[](int n) const -> const char& {
	// Potentially Implement negative indexes and create a new string using pair semantics;
	int actual = 0;
	int view = 0;
	while (string_[actual] != '\0') {
		if (predicate_(string_[actual])) {
			// This is the 'filtered' string view
			if (view == n) {
				return string_[actual];
			}
			view++;
		}
		actual++;
	}
	// Undefined Behaviour at this point (Up to the char* implementation)
	return string_[actual];
}

// Explicit std::string Coverter from fsv
view::operator std::string() const {
	if (string_ == nullptr) {
		return std::string{""};
	}
	auto fstring = std::string{string_};
	std::erase_if(fstring, [this](const char& c) { return !(predicate_(c)); });
	return fstring;
}

// =================== Member Functions ==================
// at() - Obtain char& at specified index
auto view::at(int index) const -> const char& {
	if (index < 0 or static_cast<std::size_t>(index) >= size()) {
		throw std::domain_error{"filtered_string_view::at(" + std::to_string(index) + "): invalid index"};
	}
	else {
		return (*this)[index];
	}
}

// Return size of filtered string
auto view::size() const -> std::size_t {
	if (string_ == nullptr) {
		return 0;
	}

	auto fstring = static_cast<std::string>(*this);
	return fstring.size();
}

auto view::empty() const -> bool {
	return (size() == 0);
}

// =================== Non-Member Functions ==================
// =================== Non-Member Utility Functions ==================