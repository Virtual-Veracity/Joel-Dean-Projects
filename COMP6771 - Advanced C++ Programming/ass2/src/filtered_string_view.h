#ifndef COMP6771_ASS2_FSV_H
#define COMP6771_ASS2_FSV_H

#include <compare>
#include <functional>
#include <iterator>
#include <optional>
#include <string>
#include <vector>

// find . -name '*.cpp' -o -name '*.h' | xargs clang-format -i

// =================== Filtered String View ==================
namespace fsv {
	using filter = std::function<bool(const char&)>;
	class filtered_string_view {
	 public:
		static auto default_predicate(const char& letter) -> bool {
			(void)letter;
			return true;
		};

		// Member Functions for Testing Purposes
		auto get_ustring() const -> const char* {
			return string_;
		};
		auto get_size() const -> std::size_t {
			return size_;
		};
		auto get_filter() const -> filter {
			return predicate_;
		};

		// ============== Constructors ================
		// Default Constructor
		filtered_string_view();
		// Standard String Constructors
		filtered_string_view(const std::string& str, filter predicate = default_predicate);
		// Pointer Char Constructors
		filtered_string_view(const char* str, filter predicate = default_predicate);

		// Copy Constructor
		filtered_string_view(const filtered_string_view& orig);
		// Move Constructor
		filtered_string_view(filtered_string_view&& orig) noexcept;

		// Destructor
		~filtered_string_view() = default;

		// ============== Member Operators ================
		// Copy Assignment
		auto operator=(const filtered_string_view& orig) -> filtered_string_view&;

		// Move Assignment
		auto operator=(filtered_string_view&& orig) noexcept -> filtered_string_view&;

		// Subscript Operator (non-const defaults to const)
		auto operator[](int n) const -> const char&;

		// Explicit std::string Conversion
		explicit operator std::string() const;

		// ============== Member Functions ================
		auto at(int index) const -> const char&;
		auto size() const -> std::size_t;
		auto empty() const -> bool;
		auto data() const -> const char*;
		auto predicate() const -> const filter&;

		class iter {
		 public:
			using MEMBER_TYPEDEFS_GO_HERE = void;

			iter();

			auto operator*() const -> void; // change this
			auto operator->() const -> void; // change this

			auto operator++() -> iter&;
			auto operator++(int) -> iter;
			auto operator--() -> iter&;
			auto operator--(int) -> iter;

			friend auto operator==(const iter&, const iter&) -> bool;
			friend auto operator!=(const iter&, const iter&) -> bool;

		 private:
			/* Implementation-specific private members */
		};

	 private:
		filtered_string_view(const char* str, std::size_t sz, filter pred);

		const char* string_;
		std::size_t size_;
		filter predicate_;
	};

	// // Non-Member Functions
	// auto operator==(const filtered_string_view& lhs, const filtered_string_view& rhs) -> bool;
	// auto operator!=(const filtered_string_view& lhs, const filtered_string_view& rhs) -> bool;

	// auto operator<(const filtered_string_view& lhs, const filtered_string_view& rhs) -> std::strong_ordering;
	// auto operator<(const filtered_string_view& lhs, const filtered_string_view& rhs) -> std::strong_ordering;
	// auto operator>(const filtered_string_view& lhs, const filtered_string_view& rhs) -> std::strong_ordering;
	// auto operator>=(const filtered_string_view& lhs, const filtered_string_view& rhs) -> std::strong_ordering;

	// auto operator<<(std::ostream& os, const filtered_string_view& fsv) -> std::ostream&;

	// // Non-Member Utility Functions
	// auto compose(const filtered_string_view& fsv, const std::vector<filter>& filts) -> filtered_string_view;
	// auto split(const filtered_string_view& fsv, const filtered_string_view& tok) ->
	// std::vector<filtered_string_view>; auto substr(const filtered_string_view& fsv, int pos = 0, int count = 0) ->
	// filtered_string_view;

} // namespace fsv

#endif // COMP6771_ASS2_FSV_H