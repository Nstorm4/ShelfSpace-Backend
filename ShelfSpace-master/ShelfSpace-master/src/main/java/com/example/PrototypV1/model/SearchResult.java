package com.example.PrototypV1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    @JsonProperty("volumeInfo")
    private VolumeInfo volumeInfo;

    public String getTitle() {
        if (volumeInfo != null) {
            return volumeInfo.getTitle();
        }
        return null;
    }

    public List<String> getAuthors() {
        if (volumeInfo != null) {
            return volumeInfo.getAuthors();
        }
        return Collections.emptyList();
    }

    public String getThumbnail() {
        if (volumeInfo != null && volumeInfo.getImageLinks() != null) {
            return volumeInfo.getImageLinks().getThumbnail();
        }
        return null;  // Falls kein Thumbnail vorhanden ist
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        @JsonProperty("title")
        private String title;

        @JsonProperty("authors")
        private List<String> authors;

        @JsonProperty("imageLinks")
        private ImageLinks imageLinks;

        // Getter und Setter für Titel, Autoren und ImageLinks
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

        public ImageLinks getImageLinks() {
            return imageLinks;
        }

        public void setImageLinks(ImageLinks imageLinks) {
            this.imageLinks = imageLinks;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        @JsonProperty("thumbnail")
        private String thumbnail;

        // Getter und Setter für thumbnail
        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
