package com.example.PrototypV1.service;

import com.example.PrototypV1.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Properties;

@Service
public class ShelfService {

    @Value("${shelf.properties.file}")
    private String shelfPropertiesFile;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    public void createShelf(Shelf shelf, String username) throws IOException {
        Properties properties = new Properties();
        System.out.println("I´m here mate\n" + shelfPropertiesFile);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = loader.getResourceAsStream(shelfPropertiesFile)) {
            if (input == null) {
                System.out.println("Resource nicht gefunden: " + shelfPropertiesFile);
            } else {
                properties.load(input);
                System.out.println("Properties geladen");
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Datei: " + shelfPropertiesFile);
            e.printStackTrace();
        }


        // Convert books list to JSON
        String booksJson = convertBooksToJson(shelf.getBooks());
        System.out.println("Bücherliste:\n" + booksJson);
        // Store shelf information using the username as key
        String shelfData = String.format("name:%s,books:%s", shelf.getName(), booksJson);
        properties.setProperty(username, shelfData);

        // Save properties back to file
        try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
            properties.store(output, null);
            System.out.println("Properties saved to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertBooksToJson(List<Book> books) {
        try {
            return objectMapper.writeValueAsString(books);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // Return empty JSON array in case of error
        }
    }
}
