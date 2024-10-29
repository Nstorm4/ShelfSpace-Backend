package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/api")
public class MappingController {

    private static final Logger logger = LoggerFactory.getLogger(MappingController.class);

    private final BookService bookService;
    private final UserService userService;
    private final ShelfService shelfService;
    private final TokenService tokenService;

    public MappingController(BookService bookService, UserService userService, ShelfService shelfService, TokenService tokenService) {
        this.bookService = bookService;
        this.userService = userService;
        this.shelfService = shelfService;
        this.tokenService = tokenService;
    }

    // ********** Google BOOK ENDPOINTS **********

    @GetMapping("/books")
    public String searchBooksByTitle(@RequestParam String title) {
        try {
            return bookService.searchBooksByTitle(title);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching books with title: {}", title, e);
            return e.toString();
        }
    }

    @GetMapping("/books2")
    public String searchBooksByAuthor(@RequestParam String author) {
        try {
            return bookService.searchBooksByAuthor(author, 20);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching books by author: {}", author, e);
            return e.toString();
        }
    }

    @GetMapping("/api/shelves/getRecommendation")
    public String getRecommendation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return "Ungültiges Token";
            }

            List<Shelf> shelves = shelfService.getShelvesByUsername(username);

            if (shelves == null || shelves.isEmpty()) {
                return "Keine Regale gefunden";
            }

            List<Shelf> shelvesWithBooks = shelves.stream()
                    .filter(shelf -> shelf.getBooks() != null && !shelf.getBooks().isEmpty())
                    .collect(Collectors.toList());

            if (shelvesWithBooks.isEmpty()) {
                return "Keine Regale mit Büchern gefunden";
            }

            Shelf randomShelf = shelvesWithBooks.get(new Random().nextInt(shelvesWithBooks.size()));
            Book randomBook = randomShelf.getBooks().get(new Random().nextInt(randomShelf.getBooks().size()));

            String author = randomBook.getAuthor();
            return bookService.searchBooksByAuthor(author, 12);

        } catch (Exception e) {
            logger.error("Error retrieving book recommendation", e);
            return "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage();
        }
    }

    // ********** USER ENDPOINTS **********

    @PostMapping("/api/users/register")
    public String register(@RequestBody User user) {
        if (userService.userExists(user.getUsername())) {
            logger.warn("Registration failed: Username {} already exists", user.getUsername());
            return "Benutzername existiert bereits";
        }

        userService.register(user);
        logger.info("User {} successfully registered", user.getUsername());
        return "Registrierung erfolgreich";
    }

    @PostMapping("/api/users/login")
    public String login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if (token == null) {
            logger.warn("Login failed for user: {}", user.getUsername());
            return "Ungültige Anmeldeinformationen";
        }

        logger.info("Login successful for user: {}", user.getUsername());
        return token;
    }

    @PostMapping("/api/users/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        if (isValidToken(token)) {
            userService.logout(token);
            logger.info("User with token {} successfully logged out", token);
            return "Logout erfolgreich";
        }

        logger.warn("Logout failed: Invalid token");
        return "Ungültiger Token";
    }

    // ********** SHELF ENDPOINTS **********

    @PostMapping("/api/shelves/newShelf")
    public Map<String, Object> createNewShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return Map.of("error", "Ungültiges Token");
            }

            shelfService.createShelf(shelf, username);
            return Map.of("message", "Regal erfolgreich erstellt", "shelfName", shelf.getName());
        } catch (Exception e) {
            logger.error("Error creating shelf", e);
            return Map.of("error", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/shelves/deleteShelf")
    public Map<String, Object> deleteShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return Map.of("error", "Ungültiges Token");
            }

            shelfService.deleteShelf(shelf.getName(), username);
            return Map.of("message", "Regal erfolgreich gelöscht", "shelfName", shelf.getName());
        } catch (Exception e) {
            logger.error("Error deleting shelf", e);
            return Map.of("error", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @GetMapping("/api/shelves/userShelves")
    public List<Shelf> getUserShelves(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return Collections.emptyList();  // Rückgabe eines leeren Arrays im Fehlerfall
            }

            return shelfService.getShelvesByUsername(username);
        } catch (Exception e) {
            logger.error("Error retrieving user shelves", e);
            return Collections.emptyList();
        }
    }

    @PostMapping("/api/shelves/addBook")
    public Map<String, Object> addBookToShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return Map.of("error", "Ungültiges Token");
            }

            Book newBook = new Book();
            newBook.setTitle(bookData.get("title"));
            newBook.setAuthor(bookData.get("author"));
            newBook.setCoverUrl(bookData.get("coverUrl"));

            shelfService.addBookToShelf(username, shelfName, newBook);
            return Map.of("message", "Buch erfolgreich hinzugefügt", "book", newBook);
        } catch (Exception e) {
            logger.error("Error adding book to shelf", e);
            return Map.of("error", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/shelves/deleteBook")
    public Map<String, Object> deleteBookFromShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return Map.of("error", "Ungültiges Token");
            }

            Book book = new Book();
            book.setTitle(bookData.get("title"));
            book.setAuthor(bookData.get("author"));
            book.setCoverUrl(bookData.get("coverUrl"));

            shelfService.removeBookFromShelf(username, shelfName, book);
            return Map.of("message", "Buch erfolgreich gelöscht", "book", book);
        } catch (Exception e) {
            logger.error("Error deleting book from shelf", e);
            return Map.of("error", "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // ********** TOKEN VALIDATION HELPER **********

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty() && token.startsWith("Bearer ");
    }

    private String validateAndExtractUsername(String token) {
        if (isValidToken(token)) {
            token = token.substring(7);
            return tokenService.getUserForToken(token);
        }
        return null;
    }
}
