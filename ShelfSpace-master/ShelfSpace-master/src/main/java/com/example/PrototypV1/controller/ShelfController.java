package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/newShelf")
    public ResponseEntity<?> newShelf(@RequestBody Shelf shelf) {
       shelfService.createShelf(shelf);
        return ResponseEntity.ok("Erstellen erfolgreich");
    }
}
