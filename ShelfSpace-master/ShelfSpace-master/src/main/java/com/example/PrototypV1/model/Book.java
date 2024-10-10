package com.example.PrototypV1.model;

import java.util.List;

public class Book {
    private String id;
    private String title;
    private String subtitle;
    private List<String> authors;
    private String description;
    private String thumbnailUrl;

    // Constructor
    public Book(String id, String title, String subtitle, List<String> authors, String description, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
}
