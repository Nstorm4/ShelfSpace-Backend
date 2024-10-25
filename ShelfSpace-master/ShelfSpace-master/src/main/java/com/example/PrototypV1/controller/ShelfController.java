package com.example.PrototypV1.controller;

import com.example.PrototypV1.manager.*;
import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private final ShelfService shelfService;
    private final TokenManager tokenManager;
    private BookService bookService;

    public ShelfController(ShelfService shelfService, TokenManager tokenManager, BookService bookService) {
        this.shelfService = shelfService;
        this.tokenManager = tokenManager;
        this.bookService = bookService;
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @PostMapping("/newShelf")
    public ResponseEntity<?> newShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            shelfService.createShelf(shelf, username);
            return ResponseEntity.ok(Map.of(
                    "message", "Regal erfolgreich erstellt",
                    "shelfName", shelf.getName()
            ));
               } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @DeleteMapping("/deleteShelf")
    public ResponseEntity<?> deleteShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            System.out.println("I am in deleteShelf with this username:" + username);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            shelfService.deleteShelf(shelf.getName(), username);
            return ResponseEntity.ok(Map.of(
                    "message", "Regal erfolgreich gelöscht",
                    "shelfName", shelf.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @GetMapping("/userShelves")
    public ResponseEntity<?> getUserShelves(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            List<Shelf> shelves = shelfService.getShelvesByUsername(username);
            return ResponseEntity.ok(shelves);
              } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @PostMapping("/addBook")
    public ResponseEntity<?> addBookToShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            Book newBook = new Book();
            newBook.setTitle(bookData.get("title"));
            newBook.setAuthor(bookData.get("author"));
            newBook.setCoverUrl(bookData.get("coverUrl"));

            shelfService.addBookToShelf(username, shelfName, newBook);
            return ResponseEntity.ok(Map.of("message", "Buch erfolgreich hinzugefügt", "book", newBook));
          } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @DeleteMapping("/deleteBook")
    public ResponseEntity<?> deleteBookFromShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            Book newBook = new Book();
            newBook.setTitle(bookData.get("title"));
            newBook.setAuthor(bookData.get("author"));
            newBook.setCoverUrl(bookData.get("coverUrl"));

            shelfService.removeBookFromShelf(username, shelfName, newBook);
            return ResponseEntity.ok(Map.of("message", "Buch erfolgreich gelöscht", "book", newBook));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Löschen des Buches: " + e.getMessage());
               } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @GetMapping("/getAllShelves")
    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    public ResponseEntity<?> getAllShelves() {
        try {
            String allShelves = shelfService.getAllShelves();
            return ResponseEntity.ok(allShelves);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @GetMapping("/getRecommendation")
    public String getRecommendation(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String username = validateAndExtractUsername(token);
            if (username == null) {
                return "Ungültiges Token";
            }

            List<Shelf> shelves = shelfService.getShelvesByUsername(username);

            // Überprüfen, ob überhaupt Regale vorhanden sind
            if (shelves == null || shelves.isEmpty()) {
                return "Keine Regale gefunden";
            }

            // Filtere Regale, die keine Bücher haben
            List<Shelf> shelvesWithBooks = shelves.stream()
                    .filter(shelf -> shelf.getBooks() != null && !shelf.getBooks().isEmpty())
                    .collect(Collectors.toList());

            // Überprüfen, ob nach dem Filtern noch Regale übrig sind
            if (shelvesWithBooks.isEmpty()) {
                return "Keine Regale mit Büchern gefunden";
            }

            // Zufälliges Regal mit Büchern auswählen
            Shelf randomShelf = shelvesWithBooks.get(new Random().nextInt(shelvesWithBooks.size()));

            // Zufälliges Buch aus dem Regal auswählen
            Book randomBook = randomShelf.getBooks().get(new Random().nextInt(randomShelf.getBooks().size()));

            // Den Autor des Buches zurückgeben
            String author = randomBook.getAuthor();

            return bookService.searchBooksByAuthor(author, 12);

        } catch (Exception e) {
            return "Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage();
        }
    }


    private String validateAndExtractUsername(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenManager.getUserForToken(token);
    }
}