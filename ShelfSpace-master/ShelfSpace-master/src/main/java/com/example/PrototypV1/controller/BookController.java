package com.example.PrototypV1.controller;

import com.example.PrototypV1.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
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
            return bookService.searchBooksByAuthor(author);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching books with title: {}", author, e);
            return e.toString();
        }
    }
}
