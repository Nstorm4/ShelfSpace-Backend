package com.example.ShelfSpace.controller;

import com.example.ShelfSpace.model.*;
import com.example.ShelfSpace.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
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

    // ********** BOOK ENDPOINTS **********

    @GetMapping("/book/searchByTitle")
    public ResponseEntity<List<Book>> searchBooksByTitle(@RequestParam String title) {
        try {
            List<Book> books = bookService.searchBooksByTitle(title);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching books with title: {}", title, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/book/searchByAuthor")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam String author) {
        try {
            List<Book> books = bookService.searchBooksByAuthor(author, 20);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching books with title: {}", author, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/book")
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

    @DeleteMapping("/book")
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

    @GetMapping("/book/recommendation")
    public ResponseEntity<List<Book>> getRecommendation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
            }

            List<Shelf> shelves = shelfService.getShelvesByUsername(username);

            if (shelves == null || shelves.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            List<Shelf> shelvesWithBooks = shelves.stream()
                    .filter(shelf -> shelf.getBooks() != null && !shelf.getBooks().isEmpty())
                    .collect(Collectors.toList());

            if (shelvesWithBooks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            Shelf randomShelf = shelvesWithBooks.get(new Random().nextInt(shelvesWithBooks.size()));
            Book randomBook = randomShelf.getBooks().get(new Random().nextInt(randomShelf.getBooks().size()));

            String author = randomBook.getAuthor();

            // Get the recommended books from the book service
            List<Book> recommendedBooks = bookService.searchBooksByAuthor(author, 12);

            // Check if any books were returned
            if (recommendedBooks == null || recommendedBooks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            // Return the list of recommended books
            return ResponseEntity.ok(recommendedBooks);

        } catch (Exception e) {
            logger.error("Error retrieving book recommendation", e);
            // Return an empty list with an internal server error status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ********** USER ENDPOINTS **********

    @PostMapping("/user/signIn")
    public String register(@RequestBody User user) {
        if (userService.userExists(user.getUsername())) {
            logger.warn("Registration failed: Username {} already exists", user.getUsername());
            return "Benutzername existiert bereits";
        }

        userService.register(user);
        logger.info("User {} successfully registered", user.getUsername());
        return "Registrierung erfolgreich";
    }

    @PostMapping("/user/logIn")
    public String login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if (token == null) {
            logger.warn("Login failed for user: {}", user.getUsername());
            return "Ungültige Anmeldeinformationen";
        }

        logger.info("Login successful for user: {}", user.getUsername());
        return token;
    }

    @DeleteMapping("/user")
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

    @PostMapping("/shelf")
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

    @DeleteMapping("/shelf")
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

    @GetMapping("/shelf")
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
