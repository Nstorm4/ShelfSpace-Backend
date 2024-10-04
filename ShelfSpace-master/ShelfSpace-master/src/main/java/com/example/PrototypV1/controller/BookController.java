package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.model.SearchResult;
import com.example.PrototypV1.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.*;
import java.awt.print.*;
import java.util.List;
import java.util.stream.*;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;
    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/books")
    public ResponseEntity<String> searchBooks(@RequestParam String title) {
        try {
            ResponseEntity<String> books = bookService.searchBooksByTitle(title);

//            // Extrahiere Titel, Autoren und Thumbnails
//            List<BookDTO> bookDTOs = books.stream()
//                    .map(book -> new BookDTO(book.getTitle(), book.getAuthors(), book.getThumbnail()))
//                    .collect(Collectors.toList());

            return books;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
