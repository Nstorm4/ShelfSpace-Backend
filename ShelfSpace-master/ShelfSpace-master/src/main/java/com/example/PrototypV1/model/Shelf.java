package com.example.PrototypV1.model;

import java.util.List;

public class Shelf {
    private String name;
    private List<Book> books; // Liste von Büchern im Regal

    public Shelf() {}

    public Shelf(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

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
