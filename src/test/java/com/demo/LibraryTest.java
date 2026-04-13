package com.demo;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Library System — Full Test Suite")
public class LibraryTest {

    private Library lib;

    // Sample books
    private Library.Book b1, b2, b3, b4, b5;

    // Sample members
    private Library.Member m1, m2, m3;

    @BeforeEach
    void setUp() {
        lib = new Library();

        b1 = new Library.Book("ISBN-001", "Clean Code",                "Robert Martin", "Programming", 2008);
        b2 = new Library.Book("ISBN-002", "Effective Java",            "Joshua Bloch",  "Programming", 2018);
        b3 = new Library.Book("ISBN-003", "The Pragmatic Programmer",  "David Thomas",  "Programming", 1999);
        b4 = new Library.Book("ISBN-004", "Sapiens",                   "Yuval Harari",  "History",     2011);
        b5 = new Library.Book("ISBN-005", "Thinking Fast and Slow",    "Daniel Kahneman","Psychology", 2011);

        lib.addBook(b1);
        lib.addBook(b2);
        lib.addBook(b3);
        lib.addBook(b4);
        lib.addBook(b5);

        m1 = new Library.Member("M001", "Alice",   "alice@mail.com");
        m2 = new Library.Member("M002", "Bob",     "bob@mail.com");
        m3 = new Library.Member("M003", "Charlie", "charlie@mail.com");

        lib.registerMember(m1);
        lib.registerMember(m2);
        lib.registerMember(m3);
    }

    // ════════════════════════════════════════════════
    // BOOK CREATION TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Book created with correct fields")
    void testBookFieldsSetCorrectly() {
        assertEquals("ISBN-001",     b1.getIsbn());
        assertEquals("Clean Code",   b1.getTitle());
        assertEquals("Robert Martin",b1.getAuthor());
        assertEquals("Programming",  b1.getGenre());
        assertEquals(2008,           b1.getYear());
        assertTrue(b1.isAvailable());
        assertNull(b1.getBorrowedBy());
        assertEquals(0, b1.getBorrowCount());
    }

    @Test @DisplayName("Null genre defaults to 'General'")
    void testNullGenreDefaultsToGeneral() {
        Library.Book book = new Library.Book("ISBN-X", "No Genre Book", "Author", null, 2020);
        assertEquals("General", book.getGenre());
    }

    @Test @DisplayName("Blank ISBN throws exception")
    void testBlankIsbnThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Book("", "Title", "Author", "Genre", 2020));
    }

    @Test @DisplayName("Blank title throws exception")
    void testBlankTitleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Book("ISBN-X", "  ", "Author", "Genre", 2020));
    }

    @Test @DisplayName("Null author throws exception")
    void testNullAuthorThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Book("ISBN-X", "Title", null, "Genre", 2020));
    }

    @Test @DisplayName("Invalid year throws exception")
    void testInvalidYearThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Book("ISBN-X", "Title", "Author", "Genre", 500));
    }

    @Test @DisplayName("setTitle with blank throws exception")
    void testSetTitleBlankThrows() {
        assertThrows(IllegalArgumentException.class, () -> b1.setTitle(""));
    }

    @Test @DisplayName("setAuthor with null throws exception")
    void testSetAuthorNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> b1.setAuthor(null));
    }

    @Test @DisplayName("setYear with invalid value throws exception")
    void testSetYearInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> b1.setYear(99999));
    }

    @Test @DisplayName("Book toString contains title and availability")
    void testBookToString() {
        String s = b1.toString();
        assertTrue(s.contains("Clean Code"));
        assertTrue(s.contains("Available"));
    }

    // ════════════════════════════════════════════════
    // MEMBER CREATION TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Member created with correct fields")
    void testMemberFieldsSetCorrectly() {
        assertEquals("M001",           m1.getMemberId());
        assertEquals("Alice",          m1.getName());
        assertEquals("alice@mail.com", m1.getEmail());
        assertEquals(0,                m1.getBorrowedCount());
        assertTrue(m1.canBorrow());
    }

    @Test @DisplayName("Blank member ID throws exception")
    void testBlankMemberIdThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Member("", "Name", "email"));
    }

    @Test @DisplayName("Null member name throws exception")
    void testNullMemberNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Library.Member("M999", null, "email"));
    }

    @Test @DisplayName("Null email defaults to empty string")
    void testNullEmailDefaultsToEmpty() {
        Library.Member m = new Library.Member("M999", "Name", null);
        assertEquals("", m.getEmail());
    }

    @Test @DisplayName("setName with blank throws exception")
    void testSetMemberNameBlankThrows() {
        assertThrows(IllegalArgumentException.class, () -> m1.setName("  "));
    }

    @Test @DisplayName("Member toString contains name and limits")
    void testMemberToString() {
        String s = m1.toString();
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("M001"));
    }

    // ════════════════════════════════════════════════
    // LIBRARY — ADD / REMOVE BOOK TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Total books count is correct after setup")
    void testTotalBooksAfterSetup() {
        assertEquals(5, lib.getTotalBooks());
    }

    @Test @DisplayName("Duplicate ISBN is rejected")
    void testDuplicateIsbnRejected() {
        Library.Book dup = new Library.Book("ISBN-001", "Dup Title", "Author", "Genre", 2020);
        assertFalse(lib.addBook(dup));
        assertEquals(5, lib.getTotalBooks());
    }

    @Test @DisplayName("Adding null book throws exception")
    void testAddNullBookThrows() {
        assertThrows(IllegalArgumentException.class, () -> lib.addBook(null));
    }

    @Test @DisplayName("Removing existing available book succeeds")
    void testRemoveAvailableBook() {
        assertTrue(lib.removeBook("ISBN-005"));
        assertEquals(4, lib.getTotalBooks());
    }

    @Test @DisplayName("Removing non-existent ISBN returns false")
    void testRemoveNonExistentBook() {
        assertFalse(lib.removeBook("ISBN-999"));
    }

    @Test @DisplayName("Removing a currently borrowed book throws exception")
    void testRemoveBorrowedBookThrows() {
        lib.borrowBook("ISBN-001", "M001");
        assertThrows(IllegalStateException.class, () -> lib.removeBook("ISBN-001"));
    }

    @Test @DisplayName("findBookByIsbn returns correct book")
    void testFindBookByIsbn() {
        Optional<Library.Book> found = lib.findBookByIsbn("ISBN-004");
        assertTrue(found.isPresent());
        assertEquals("Sapiens", found.get().getTitle());
    }

    @Test @DisplayName("findBookByIsbn returns empty for unknown ISBN")
    void testFindBookByIsbnMissing() {
        assertFalse(lib.findBookByIsbn("ISBN-999").isPresent());
    }

    // ════════════════════════════════════════════════
    // LIBRARY — SEARCH TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Search by title partial match works")
    void testSearchByTitlePartial() {
        List<Library.Book> results = lib.searchByTitle("code");
        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test @DisplayName("Search by title is case-insensitive")
    void testSearchByTitleCaseInsensitive() {
        List<Library.Book> results = lib.searchByTitle("EFFECTIVE");
        assertEquals(1, results.size());
    }

    @Test @DisplayName("Search by author partial match works")
    void testSearchByAuthorPartial() {
        List<Library.Book> results = lib.searchByAuthor("bloch");
        assertEquals(1, results.size());
        assertEquals("Joshua Bloch", results.get(0).getAuthor());
    }

    @Test @DisplayName("Search by genre returns all matching books")
    void testSearchByGenre() {
        List<Library.Book> results = lib.searchByGenre("programming");
        assertEquals(3, results.size());
    }

    @Test @DisplayName("Search with blank keyword returns empty list")
    void testSearchBlankKeywordReturnsEmpty() {
        assertTrue(lib.searchByTitle("").isEmpty());
        assertTrue(lib.searchByAuthor("  ").isEmpty());
        assertTrue(lib.searchByGenre(null).isEmpty());
    }

    @Test @DisplayName("Search returns empty list when no match")
    void testSearchNoMatch() {
        assertTrue(lib.searchByTitle("XYZ Unknown Title").isEmpty());
    }

    // ════════════════════════════════════════════════
    // LIBRARY — REGISTER / REMOVE MEMBER TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Total members count is correct after setup")
    void testTotalMembersAfterSetup() {
        assertEquals(3, lib.getTotalMembers());
    }

    @Test @DisplayName("Duplicate member ID is rejected")
    void testDuplicateMemberRejected() {
        Library.Member dup = new Library.Member("M001", "Duplicate", "dup@mail.com");
        assertFalse(lib.registerMember(dup));
        assertEquals(3, lib.getTotalMembers());
    }

    @Test @DisplayName("Adding null member throws exception")
    void testRegisterNullMemberThrows() {
        assertThrows(IllegalArgumentException.class, () -> lib.registerMember(null));
    }

    @Test @DisplayName("Removing member with no borrowed books succeeds")
    void testRemoveMemberNoBorrows() {
        assertTrue(lib.removeMember("M003"));
        assertEquals(2, lib.getTotalMembers());
    }

    @Test @DisplayName("Removing member with borrowed books throws exception")
    void testRemoveMemberWithBorrowsThrows() {
        lib.borrowBook("ISBN-001", "M001");
        assertThrows(IllegalStateException.class, () -> lib.removeMember("M001"));
    }

    @Test @DisplayName("Removing non-existent member returns false")
    void testRemoveNonExistentMember() {
        assertFalse(lib.removeMember("M999"));
    }

    @Test @DisplayName("findMemberById returns correct member")
    void testFindMemberById() {
        Optional<Library.Member> found = lib.findMemberById("M002");
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    // ════════════════════════════════════════════════
    // BORROW & RETURN TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Borrowing available book succeeds")
    void testBorrowSuccess() {
        assertTrue(lib.borrowBook("ISBN-001", "M001"));
        assertFalse(b1.isAvailable());
        assertEquals("Alice", b1.getBorrowedBy());
        assertEquals(1, b1.getBorrowCount());
        assertTrue(m1.getBorrowedIsbns().contains("ISBN-001"));
    }

    @Test @DisplayName("Borrowing already borrowed book returns false")
    void testBorrowAlreadyBorrowed() {
        lib.borrowBook("ISBN-001", "M001");
        assertFalse(lib.borrowBook("ISBN-001", "M002"));
    }

    @Test @DisplayName("Member cannot borrow beyond limit of 3")
    void testBorrowLimitEnforced() {
        lib.borrowBook("ISBN-001", "M001");
        lib.borrowBook("ISBN-002", "M001");
        lib.borrowBook("ISBN-003", "M001");
        assertFalse(m1.canBorrow());
        assertFalse(lib.borrowBook("ISBN-004", "M001")); // 4th borrow blocked
    }

    @Test @DisplayName("Borrow count increments correctly across multiple borrows")
    void testBorrowCountIncrements() {
        lib.borrowBook("ISBN-001", "M001");
        lib.returnBook("ISBN-001", "M001");
        lib.borrowBook("ISBN-001", "M002");
        assertEquals(2, b1.getBorrowCount());
    }

    @Test @DisplayName("Borrowing non-existent book throws exception")
    void testBorrowNonExistentBookThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            lib.borrowBook("ISBN-999", "M001"));
    }

    @Test @DisplayName("Borrowing with non-existent member throws exception")
    void testBorrowNonExistentMemberThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            lib.borrowBook("ISBN-001", "M999"));
    }

    @Test @DisplayName("Returning a borrowed book succeeds")
    void testReturnSuccess() {
        lib.borrowBook("ISBN-002", "M002");
        assertTrue(lib.returnBook("ISBN-002", "M002"));
        assertTrue(b2.isAvailable());
        assertNull(b2.getBorrowedBy());
        assertFalse(m2.getBorrowedIsbns().contains("ISBN-002"));
    }

    @Test @DisplayName("Returning a book not currently borrowed returns false")
    void testReturnNotBorrowed() {
        assertFalse(lib.returnBook("ISBN-001", "M001")); // never borrowed
    }

    @Test @DisplayName("Returning a book borrowed by a different member returns false")
    void testReturnWrongMember() {
        lib.borrowBook("ISBN-001", "M001");
        assertFalse(lib.returnBook("ISBN-001", "M002")); // M002 didn't borrow it
    }

    @Test @DisplayName("Returning non-existent book throws exception")
    void testReturnNonExistentBookThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            lib.returnBook("ISBN-999", "M001"));
    }

    // ════════════════════════════════════════════════
    // STATISTICS TESTS
    // ════════════════════════════════════════════════

    @Test @DisplayName("Available books count is correct after borrows")
    void testAvailableBooksCount() {
        lib.borrowBook("ISBN-001", "M001");
        lib.borrowBook("ISBN-002", "M002");
        assertEquals(3, lib.getAvailableBooks().size());
        assertEquals(2, lib.getBorrowedBooks().size());
    }

    @Test @DisplayName("Availability rate is 100% when all books available")
    void testAvailabilityRateFull() {
        assertEquals(100.0, lib.getAvailabilityRate(), 0.01);
    }

    @Test @DisplayName("Availability rate is correct after some borrows")
    void testAvailabilityRatePartial() {
        lib.borrowBook("ISBN-001", "M001");
        lib.borrowBook("ISBN-002", "M002");
        assertEquals(60.0, lib.getAvailabilityRate(), 0.01); // 3 of 5 available
    }

    @Test @DisplayName("Availability rate is 0% on empty library")
    void testAvailabilityRateEmpty() {
        Library empty = new Library();
        assertEquals(0.0, empty.getAvailabilityRate(), 0.01);
    }

    @Test @DisplayName("Genre count map is correct")
    void testGenreCountMap() {
        Map<String, Long> genreMap = lib.getBookCountByGenre();
        assertEquals(3L, genreMap.get("Programming"));
        assertEquals(1L, genreMap.get("History"));
        assertEquals(1L, genreMap.get("Psychology"));
    }

    @Test @DisplayName("getMostBorrowedBooks returns correct top N")
    void testMostBorrowedBooks() {
        lib.borrowBook("ISBN-001", "M001"); lib.returnBook("ISBN-001", "M001");
        lib.borrowBook("ISBN-001", "M002"); lib.returnBook("ISBN-001", "M002");
        lib.borrowBook("ISBN-002", "M001"); lib.returnBook("ISBN-002", "M001");

        List<Library.Book> top = lib.getMostBorrowedBooks(2);
        assertEquals(2, top.size());
        assertEquals("ISBN-001", top.get(0).getIsbn()); // 2 borrows
        assertEquals("ISBN-002", top.get(1).getIsbn()); // 1 borrow
    }
}