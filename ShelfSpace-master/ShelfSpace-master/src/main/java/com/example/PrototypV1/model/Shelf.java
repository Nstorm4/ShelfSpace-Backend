package com.example.PrototypV1.model;

import com.example.PrototypV1.model.*;

import java.util.*;

public class Shelf {
    private String name;
    private User user;  // Assuming there is a User class with a username property
    private List<Book> books;

    // Constructor, Getter und Setter
    public Shelf(String name, User user, List<Book> books) {
        this.name = name;
        this.user = user;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
