package com.example.PrototypV1.service;

import com.example.PrototypV1.model.Book;
import com.example.PrototypV1.model.Shelf;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ShelfService {

    @Value("${shelf.properties.file}")
    private String shelfPropertiesFile;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    /**
     * Speichert ein neues Regal für den Benutzer.
     */
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

        // Überprüfen, ob der Benutzer schon Regale hat
        String existingShelvesJson = properties.getProperty(username);
        List<Shelf> shelves = new ArrayList<>();

        // Wenn bereits Regale existieren, lade sie in die Liste
        if (existingShelvesJson != null && !existingShelvesJson.trim().isEmpty()) {
            shelves = objectMapper.readValue(existingShelvesJson, new TypeReference<List<Shelf>>() {});
        }

        // Neues Regal zur Liste hinzufügen
        shelves.add(shelf);

        // Speichere die aktualisierte Liste als JSON-Array
        String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
        properties.setProperty(username, updatedShelvesJson);

        // Speichern in die Datei
        try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
            properties.store(output, null);
            System.out.println("Regale erfolgreich gespeichert.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lädt alle Regale eines Benutzers aus der Properties-Datei.
     */
    public List<Shelf> getShelvesByUsername(String username) {
        Properties properties = new Properties();
        List<Shelf> shelves = new ArrayList<>();

        // Laden der Properties-Datei
        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);

            // Abfragen des Regal-Strings für den Benutzer
            String shelfData = properties.getProperty(username);
            if (shelfData != null && !shelfData.isEmpty()) {
                try {
                    // JSON-String direkt in Liste von Shelf-Objekten konvertieren
                    shelves = objectMapper.readValue(shelfData, new TypeReference<List<Shelf>>() {});
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shelves;
    }

    /**
     * Konvertiert eine Liste von Book-Objekten in einen JSON-String.
     */
    private String convertBooksToJson(List<Book> books) {
        try {
            return objectMapper.writeValueAsString(books);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]"; // Leeres JSON-Array im Fehlerfall
        }
    }

    /**
     * Konvertiert einen JSON-String zu einer Liste von Book-Objekten.
     */
    private List<Book> convertJsonToBooks(String booksJson) {
        try {
            // Konvertiere den JSON-String zurück in eine Liste von Book-Objekten
            return objectMapper.readValue(booksJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Leere Liste im Fehlerfall
        }
    }
}
