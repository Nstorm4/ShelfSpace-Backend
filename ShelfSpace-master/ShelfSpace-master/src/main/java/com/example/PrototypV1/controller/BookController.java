package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:63342") // Or specify your frontend URL
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        try {
            List<Book> books = bookService.searchBooksByTitle(title);
            return ResponseEntity.ok(books);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/HomePage")
    public ResponseEntity<List<Bookshelf>> getBookshelves() {
        List<Bookshelf> bookshelves = bookService.getAllBookshelves();
        return ResponseEntity.ok(bookshelves);
    }

    @PostMapping("/HomePage")
    public ResponseEntity<Bookshelf> createBookshelf(@RequestBody BookshelfRequest request) {
        try {
            // This is a bug. The BookshelfRequest class doesn't have a getBookIds() method.
            // We need to add this method to the BookshelfRequest class.
            // For now, we'll use an empty list as a temporary fix.
            Bookshelf bookshelf = bookService.createBookshelf(request.getName(), List.of());
            return ResponseEntity.ok(bookshelf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("HomePage")
    public ResponseEntity<Bookshelf> addBookToShelf(@RequestBody AddBookRequest request) {
        try {
            Bookshelf updatedShelf = bookService.addBookToShelf(request.getShelfName(), request.getBookId());
            return ResponseEntity.ok(updatedShelf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

class BookshelfRequest {
    private String name;
    private List<String> bookIds;

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<String> bookIds) {
        this.bookIds = bookIds;
    }
    
    
}

class AddBookRequest {
    private String shelfName;
    private String bookId;

    // getters and setters
    public String getShelfName() {
        return shelfName;
    }

    public String getBookId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookId'");
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }
    
    
}
