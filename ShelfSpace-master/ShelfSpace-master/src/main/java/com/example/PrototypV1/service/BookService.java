package com.example.PrototypV1.service;

import com.example.PrototypV1.model.Book;
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

        // Überprüfen, ob "items" ein Array ist und nicht leer ist
        if (items.isArray() && items.size() > 0) {
            for (JsonNode item : items) {
                JsonNode volumeInfo = item.path("volumeInfo");

                // Sicherstellen, dass "volumeInfo" vorhanden ist
                if (volumeInfo != null) {
                    String title = volumeInfo.path("title").asText("Unbekannter Titel"); // Default-Wert
                    String author = volumeInfo.path("authors").isArray() && volumeInfo.path("authors").size() > 0
                            ? volumeInfo.path("authors").get(0).asText("Unbekannter Autor") // Default-Wert
                            : "Unbekannter Autor"; // Fallback
                    String coverUrl = volumeInfo.path("imageLinks").path("thumbnail").asText("Keine Cover-URL verfügbar"); // Default-Wert

                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setCoverUrl(coverUrl);

                    books.add(book);
                }
            }
        } else {
            // Optional: Logik, wenn keine Bücher gefunden werden
            System.out.println("Keine Bücher gefunden.");
        }

        return books;
    }
}
