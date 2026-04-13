package com.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
    }

    @Test
    void testAddBook() {
        libraryService.addBook("Clean Code");
        assertEquals(1, libraryService.getBookCount());
    }

    @Test
    void testAddMultipleBooks() {
        libraryService.addBook("Clean Code");
        libraryService.addBook("Effective Java");
        assertEquals(2, libraryService.getBookCount());
    }

    @Test
    void testRemoveBook() {
        libraryService.addBook("Clean Code");
        libraryService.removeBook("Clean Code");
        assertEquals(0, libraryService.getBookCount());
    }

    @Test
    void testBookExists() {
        libraryService.addBook("Clean Code");
        assertTrue(libraryService.bookExists("Clean Code"));
    }

    @Test
    void testBookNotExists() {
        assertFalse(libraryService.bookExists("Unknown Book"));
    }
}