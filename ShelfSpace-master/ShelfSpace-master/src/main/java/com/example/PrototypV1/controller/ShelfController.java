package com.example.PrototypV1.controller;

import com.example.PrototypV1.manager.TokenManager;
import com.example.PrototypV1.model.Shelf;
import com.example.PrototypV1.service.ShelfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private final ShelfService shelfService;
    private final TokenManager tokenManager;

    public ShelfController(ShelfService shelfService, TokenManager tokenManager) {
        this.shelfService = shelfService;
        this.tokenManager = tokenManager;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/newShelf")
    public ResponseEntity<?> newShelf(@RequestBody Shelf shelf, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws IOException {
        // Token Format: "Bearer <token>", wir müssen "Bearer " entfernen, um nur das Token zu bekommen
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Entfernt "Bearer "
        }

        // Überprüfen, ob das Token gültig ist
        String username = tokenManager.getUserForToken(token);
        System.out.println("Empfangenes Token: " + token);
        System.out.println("Gefundener Benutzer für Token: " + username);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
        }
        System.out.println("Empfangenes Token: " + token);
        System.out.println("Gefundener Benutzer für Token: " + username);

        // Wenn das Token gültig ist, Regal erstellen
        shelfService.createShelf(shelf, username);
        System.out.println("Regal erfolgreich erstellt");

        // Rückgabe eines strukturierten JSON-Objekts
        return ResponseEntity.ok(Map.of(
                "message", "Regal erfolgreich erstellt",
                "shelfName", shelf.getName() // Regalname zurückgeben
        ));
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/userShelves")
    public ResponseEntity<?> getUserShelves(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Token Format: "Bearer <token>", wir müssen "Bearer " entfernen, um nur das Token zu bekommen
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Entfernt "Bearer "
        }

        // Überprüfen, ob das Token gültig ist
        String username = tokenManager.getUserForToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
        }

        // Regale für den Benutzer abrufen
        List<Shelf> shelves = shelfService.getShelvesByUsername(username);
        return ResponseEntity.ok(shelves);
    }

}
