package com.example.PrototypV1.controller;

import com.example.PrototypV1.manager.*;
import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private final ShelfService shelfService;
    private final TokenManager tokenManager;

    public ShelfController(ShelfService shelfService, TokenManager tokenManager) {
        this.shelfService = shelfService;
        this.tokenManager = tokenManager;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/newShelf")
    public ResponseEntity<?> newShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // Entfernt "Bearer "
//            }
//
//            String username = tokenManager.getUserForToken(token);

            String username = validateAndExtractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            shelfService.createShelf(shelf, username);
            return ResponseEntity.ok(Map.of(
                    "message", "Regal erfolgreich erstellt",
                    "shelfName", shelf.getName()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Erstellen des Regals: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/deleteShelf")
    public ResponseEntity<?> deleteShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // Entfernt "Bearer "
//            }
//
//            String username = tokenManager.getUserForToken(token);

            String username = validateAndExtractUsername(token);

            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
            }

            shelfService.deleteShelf(shelf, username);
            return ResponseEntity.ok(Map.of(
                    "message", "Regal erfolgreich gelöscht",
                    "shelfName", shelf.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/userShelves")
    public ResponseEntity<?> getUserShelves(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // Entfernt "Bearer "
//            }
//
//            String username = tokenManager.getUserForToken(token);

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

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/addBook")
    public ResponseEntity<?> addBookToShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");

//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // Entfernt "Bearer "
//            }
//
//            String username = tokenManager.getUserForToken(token);
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
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Hinzufügen des Buches: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/deleteBook")
    public ResponseEntity<?> deleteBookFromShelf(@RequestBody Map<String, Object> payload, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String shelfName = (String) payload.get("shelfName");
            Map<String, String> bookData = (Map<String, String>) payload.get("book");

//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // Entfernt "Bearer "
//            }
//
//            String username = tokenManager.getUserForToken(token);
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
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> getAllShelves() {
        try {
            String allShelves = shelfService.getAllShelves();
            return ResponseEntity.ok(allShelves);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    private String validateAndExtractUsername(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return tokenManager.getUserForToken(token);
    }
}