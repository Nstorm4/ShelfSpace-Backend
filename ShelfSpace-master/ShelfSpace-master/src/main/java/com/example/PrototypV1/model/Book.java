package com.example.PrototypV1.model;

import jakarta.persistence.*;


@Entity
@Table(name = "ShelfSpaceBooks")
public class Book {

 @Id
    private String title;
    private String author;
    private String coverUrl;



    public Book(String title, String author, String coverUrl) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public Book() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
