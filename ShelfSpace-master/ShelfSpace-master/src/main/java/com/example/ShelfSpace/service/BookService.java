package com.example.ShelfSpace.service;

import com.example.ShelfSpace.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Book> searchBooksByTitle(String title) {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + encodedTitle + "&maxResults=20&key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));
            return extractBooksFromResponse(response.getBody());
        } catch (Exception e) {
            logger.error("Error occurred while calling Google Books API with title: {} and url: {}", title, url, e);
            return new ArrayList<>();
        }
    }

    public List<Book> searchBooksByAuthor(String author, int maxResults) {
        String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/books/v1/volumes?q=inauthor:" + encodedAuthor + "&maxResults=" + maxResults + "&key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));
            return extractBooksFromResponse(response.getBody());
        } catch (Exception e) {
            logger.error("Error occurred while calling Google Books API with title: {} and url: {}", author, url, e);
            return new ArrayList<>();
        }
    }

    private List<Book> extractBooksFromResponse(String jsonResponse) throws Exception {
        List<Book> books = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode items = root.path("items");

        if (items.isArray() && items.size() > 0) {
            for (JsonNode item : items) {
                JsonNode volumeInfo = item.path("volumeInfo");

                if (volumeInfo != null) {
                    String title = volumeInfo.path("title").asText("Unbekannter Titel");
                    String author = volumeInfo.path("authors").isArray() && volumeInfo.path("authors").size() > 0
                            ? volumeInfo.path("authors").get(0).asText("Unbekannter Autor")
                            : "Unbekannter Autor"; // Fallback
                    String coverUrl = volumeInfo.path("imageLinks").path("thumbnail").asText("Keine Cover-URL verfügbar");

                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setCoverUrl(coverUrl);

                    books.add(book);
                }
            }
        } else {
            logger.error("Keine Bücher gefunden.");
        }
        return books;
    }
}
