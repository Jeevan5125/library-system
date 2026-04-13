package com.demo;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    private List<String> books = new ArrayList<>();

    public void addBook(String title) {
        books.add(title);
    }

    public void removeBook(String title) {
        books.remove(title);
    }

    public int getBookCount() {
        return books.size();
    }

    public boolean bookExists(String title) {
        return books.contains(title);
    }
}