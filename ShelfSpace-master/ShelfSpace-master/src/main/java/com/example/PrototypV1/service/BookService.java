package com.example.PrototypV1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String searchBooksByTitle(String title) {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + encodedTitle + "&maxResults=20&key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while calling Google Books API with title: {} and url: {}", title, url, e);
            return e.toString();
        }
    }

    public String searchBooksByAuthor(String author, int maxResults) {
        String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/books/v1/volumes?q=inauthor:" + encodedAuthor + "&maxResults=" + maxResults + "&key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred while calling Google Books API with title: {} and url: {}", author, url, e);
            return e.toString();
        }
    }
}
