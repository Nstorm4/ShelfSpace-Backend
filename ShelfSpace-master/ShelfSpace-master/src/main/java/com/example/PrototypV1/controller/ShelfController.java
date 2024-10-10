package com.example.PrototypV1.controller;

import com.example.PrototypV1.manager.TokenManager;
import com.example.PrototypV1.model.Shelf;
import com.example.PrototypV1.service.ShelfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;

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
        if (username != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültiges Token");
        }

        // Wenn das Token gültig ist, Regal erstellen
        shelfService.createShelf(shelf, username);
        return ResponseEntity.ok("Regal erfolgreich erstellt");
    }
}
