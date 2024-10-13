package com.example.PrototypV1.controller;

import com.example.PrototypV1.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/books")
    public String searchBooks(@RequestParam String title) {
        try {
            String books = bookService.searchBooksByTitle(title);
            return books;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
