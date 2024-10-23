package com.example.PrototypV1.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ShelfSpaceUsers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)  // Username ist einzigartig und darf nicht null sein
    private String username;

    @Column(nullable = false)  // Username ist einzigartig und darf nicht null sein
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shelf> shelves;

    // Getter, Setter, Constructors

    public User() {}

// Konstruktor mit Parametern
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter und Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(List<Shelf> shelves) {
        this.shelves = shelves;
    }
}
