package com.example.PrototypV1.model;

import java.util.*;

public class BookDTO {
    private String title;
    private List<String> authors;
    private String thumbnail;

    // Book-Data-Transfer-Object
    // => Ist das Java Object welches aus dem JSON der Google API request erstellt wird

    public BookDTO(String title, List<String> authors, String thumbnail) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
    }

    // Getter und Setter
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

    public String getthumbnail() {
        return thumbnail;
    }

    public void setthumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
