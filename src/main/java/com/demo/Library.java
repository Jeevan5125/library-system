package com.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {

    // Inner Book class
    public static class Book {
        private final String isbn;
        private String title;
        private String author;
        private String genre;
        private int year;
        private boolean isAvailable;
        private String borrowedBy;
        private int borrowCount;

        public Book(String isbn, String title, String author, String genre, int year) {
            if (isbn == null || isbn.isBlank())    throw new IllegalArgumentException("ISBN cannot be empty");
            if (title == null || title.isBlank())  throw new IllegalArgumentException("Title cannot be empty");
            if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty");
            if (year < 1000 || year > 2100)        throw new IllegalArgumentException("Invalid publication year");

            this.isbn        = isbn;
            this.title       = title;
            this.author      = author;
            this.genre       = genre == null ? "General" : genre;
            this.year        = year;
            this.isAvailable = true;
            this.borrowedBy  = null;
            this.borrowCount = 0;
        }

        public String getIsbn()       { return isbn; }
        public String getTitle()      { return title; }
        public String getAuthor()     { return author; }
        public String getGenre()      { return genre; }
        public int    getYear()       { return year; }
        public boolean isAvailable()  { return isAvailable; }
        public String getBorrowedBy() { return borrowedBy; }
        public int    getBorrowCount(){ return borrowCount; }

        public void setTitle(String title) {
            if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty");
            this.title = title;
        }
        public void setAuthor(String author) {
            if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty");
            this.author = author;
        }
        public void setGenre(String genre)  { this.genre = genre; }
        public void setYear(int year) {
            if (year < 1000 || year > 2100) throw new IllegalArgumentException("Invalid year");
            this.year = year;
        }
        void checkOut(String memberName) {
            this.isAvailable = false;
            this.borrowedBy  = memberName;
            this.borrowCount++;
        }
        void checkIn() {
            this.isAvailable = true;
            this.borrowedBy  = null;
        }

        @Override
        public String toString() {
            return String.format("[%s] \"%s\" by %s (%d, %s) — %s",
                isbn, title, author, year, genre,
                isAvailable ? "Available" : "Checked out by " + borrowedBy);
        }
    }

    // Inner Member class
    public static class Member {
        private final String memberId;
        private String name;
        private String email;
        private final List<String> borrowedIsbns;
        private static final int MAX_BORROW_LIMIT = 3;

        public Member(String memberId, String name, String email) {
            if (memberId == null || memberId.isBlank()) throw new IllegalArgumentException("Member ID cannot be empty");
            if (name == null || name.isBlank())         throw new IllegalArgumentException("Name cannot be empty");

            this.memberId     = memberId;
            this.name         = name;
            this.email        = email == null ? "" : email;
            this.borrowedIsbns = new ArrayList<>();
        }

        public String       getMemberId()      { return memberId; }
        public String       getName()          { return name; }
        public String       getEmail()         { return email; }
        public List<String> getBorrowedIsbns() { return new ArrayList<>(borrowedIsbns); }
        public int          getBorrowedCount() { return borrowedIsbns.size(); }
        public boolean      canBorrow()        { return borrowedIsbns.size() < MAX_BORROW_LIMIT; }

        public void setName(String name) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
            this.name = name;
        }
        public void setEmail(String email) { this.email = email; }

        void addBorrow(String isbn)  { borrowedIsbns.add(isbn); }
        void removeBorrow(String isbn) { borrowedIsbns.remove(isbn); }

        @Override
        public String toString() {
            return String.format("Member[%s] %s <%s> — Borrowed: %d/%d",
                memberId, name, email, borrowedIsbns.size(), MAX_BORROW_LIMIT);
        }
    }

    // ── Library state ──────────────────────────────────────────────
    private final Map<String, Book>   books   = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();

    // ── Book management ────────────────────────────────────────────

    public boolean addBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");
        if (books.containsKey(book.getIsbn())) return false;
        books.put(book.getIsbn(), book);
        return true;
    }

    public boolean removeBook(String isbn) {
        Book book = books.get(isbn);
        if (book == null) return false;
        if (!book.isAvailable()) throw new IllegalStateException("Cannot remove a book that is currently borrowed");
        books.remove(isbn);
        return true;
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    public List<Book> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>();
        String lower = keyword.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>();
        String lower = keyword.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Book> searchByGenre(String genre) {
        if (genre == null || genre.isBlank()) return new ArrayList<>();
        String lower = genre.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getGenre().toLowerCase().equals(lower))
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Book> getBorrowedBooks() {
        return books.values().stream()
                .filter(b -> !b.isAvailable())
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooks() { return new ArrayList<>(books.values()); }
    public int getTotalBooks()      { return books.size(); }

    // Most borrowed books (top N)
    public List<Book> getMostBorrowedBooks(int topN) {
        return books.values().stream()
                .sorted((a, b) -> b.getBorrowCount() - a.getBorrowCount())
                .limit(topN)
                .collect(Collectors.toList());
    }

    // ── Member management ──────────────────────────────────────────

    public boolean registerMember(Member member) {
        if (member == null) throw new IllegalArgumentException("Member cannot be null");
        if (members.containsKey(member.getMemberId())) return false;
        members.put(member.getMemberId(), member);
        return true;
    }

    public boolean removeMember(String memberId) {
        Member member = members.get(memberId);
        if (member == null) return false;
        if (member.getBorrowedCount() > 0)
            throw new IllegalStateException("Cannot remove member with unreturned books");
        members.remove(memberId);
        return true;
    }

    public Optional<Member> findMemberById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    public List<Member> getAllMembers() { return new ArrayList<>(members.values()); }
    public int getTotalMembers()        { return members.size(); }

    // ── Borrow & Return ────────────────────────────────────────────

    public boolean borrowBook(String isbn, String memberId) {
        Book book = books.get(isbn);
        if (book == null)   throw new IllegalArgumentException("Book not found: " + isbn);

        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found: " + memberId);

        if (!book.isAvailable())  return false;  // already borrowed
        if (!member.canBorrow())  return false;  // borrow limit reached

        book.checkOut(member.getName());
        member.addBorrow(isbn);
        return true;
    }

    public boolean returnBook(String isbn, String memberId) {
        Book book = books.get(isbn);
        if (book == null)   throw new IllegalArgumentException("Book not found: " + isbn);

        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found: " + memberId);

        if (book.isAvailable()) return false;  // wasn't borrowed
        if (!member.getBorrowedIsbns().contains(isbn)) return false;  // not borrowed by this member

        book.checkIn();
        member.removeBorrow(isbn);
        return true;
    }

    // ── Statistics ─────────────────────────────────────────────────

    public Map<String, Long> getBookCountByGenre() {
        return books.values().stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
    }

    public double getAvailabilityRate() {
        if (books.isEmpty()) return 0.0;
        long available = books.values().stream().filter(Book::isAvailable).count();
        return (double) available / books.size() * 100.0;
    }
}