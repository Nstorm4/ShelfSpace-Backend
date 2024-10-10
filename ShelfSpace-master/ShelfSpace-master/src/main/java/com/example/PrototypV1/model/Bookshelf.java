package com.example.PrototypV1.model;

import java.util.ArrayList;
import java.util.List;

public class Bookshelf {
    private String name;
    private List<Book> books;

    public Bookshelf(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public List<Book> getBooks() {
        return books;
    }

    // Getters and setters for name
    // ... (implement getters and setters for name)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}