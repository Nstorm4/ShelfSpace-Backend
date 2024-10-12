package com.example.PrototypV1.model;

import java.util.List;

public class Shelf {
    private String name;
    private List<Book> books;

    // Standard-Konstruktor
    public Shelf() {
    }

    // Konstruktor mit Parametern (falls benötigt)
    public Shelf(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

    // Getter und Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
