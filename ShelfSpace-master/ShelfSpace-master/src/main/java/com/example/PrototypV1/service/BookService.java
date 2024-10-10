package com.example.PrototypV1.service;

import com.example.PrototypV1.model.Book;
import com.example.PrototypV1.model.Bookshelf;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BookService {

    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Bookshelf> bookshelves = new ArrayList<>(); // This is temporary. In a real application, you'd use a database.

    public List<Book> searchBooksByTitle(String title) throws JsonProcessingException {
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&maxResults=20&key={apiKey}";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode items = root.path("items");

        List<Book> books = new ArrayList<>();
        for (JsonNode item : items) {
            Book book = extractBookFromJson(item);
            books.add(book);
        }

        return books;
    }

    private Book extractBookFromJson(JsonNode bookNode) {
        String id = bookNode.path("id").asText();
        JsonNode volumeInfo = bookNode.path("volumeInfo");
        String title = volumeInfo.path("title").asText();
        String subtitle = volumeInfo.path("subtitle").asText();
        List<String> authors = new ArrayList<>();
        for (JsonNode author : volumeInfo.path("authors")) {
            authors.add(author.asText());
        }
        String description = volumeInfo.path("description").asText();
        String thumbnail = volumeInfo.path("imageLinks").path("thumbnail").asText();

        return new Book(id, title, subtitle, authors, description, thumbnail);
    }

    public List<Bookshelf> getAllBookshelves() {
        return bookshelves;
    }

    public Bookshelf createBookshelf(String name, List<String> bookIds) throws JsonProcessingException {
        Bookshelf bookshelf = new Bookshelf(name);
        for (String bookId : bookIds) {
            Book book = getBookById(bookId);
            if (book != null) {
                bookshelf.addBook(book);
            }
        }
        bookshelves.add(bookshelf);
        return bookshelf;
    }

    public Bookshelf addBookToShelf(String shelfName, String bookId) throws JsonProcessingException {
        Bookshelf bookshelf = bookshelves.stream()
                .filter(shelf -> shelf.getName().equals(shelfName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

        Book book = getBookById(bookId);
        if (book != null) {
            bookshelf.addBook(book);
        }

        return bookshelf;
    }

    private Book getBookById(String bookId) throws JsonProcessingException {
        String url = "https://www.googleapis.com/books/v1/volumes/" + bookId + "?key={apiKey}";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));

        JsonNode root = objectMapper.readTree(response.getBody());
        return extractBookFromJson(root);
    }
}