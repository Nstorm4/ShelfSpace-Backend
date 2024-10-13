package com.example.PrototypV1.model;

public class Book {
    private String title;
    private String author;
    private String coverUrl; // URL zum Buchcover

    // Konstruktoren
    public Book() {}

    public Book(String title, String author, String coverUrl) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    // Getter und Setter für Titel
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter und Setter für Autor
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    // Getter und Setter für Cover-URL
    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
