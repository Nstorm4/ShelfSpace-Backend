package com.example.PrototypV1.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ShelfSpaceShelves")
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Referenz auf den Benutzer, dem das Regal gehört

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shelf_id")
    private List<Book> books;

    public Shelf() {}

    public Shelf(String name, User user) {
        this.name = name;
        this.user = user;
    }

public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
