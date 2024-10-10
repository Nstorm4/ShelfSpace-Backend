package com.example.PrototypV1.model;

import java.util.List;

public class Book {
    private String id;
    private String thumbnailUrl;
    private String title;
    private List<String> authors; // Liste von Autoren

    // Constructor, Getter und Setter
    public Book(String id, String thumbnailUrl, String title, List<String> authors) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.authors = authors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
