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

    private List<Book> convertJsonToBooks(String booksJson) {
        try {
            // JSON-String zurück in Liste von Book-Objekten konvertieren, die eine coverUrl enthalten
            return objectMapper.readValue(booksJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Leere Liste im Fehlerfall
        }
    }

    public void addBookToShelf(String username, String shelfName, Book book) throws IOException {
        Properties properties = new Properties();

        // Laden der Properties-Datei
        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);

            // Bestehende Regale des Benutzers laden
            String shelfData = properties.getProperty(username);
            if (shelfData != null && !shelfData.isEmpty()) {
                List<Shelf> shelves = objectMapper.readValue(shelfData, new TypeReference<List<Shelf>>() {});

                boolean shelfFound = false;
                // Regal finden und Buch hinzufügen
                for (Shelf shelf : shelves) {
                    if (shelf.getName().equals(shelfName)) {
                        if (shelf.getBooks() == null) {
                            shelf.setBooks(new ArrayList<>());
                        }
                        shelf.getBooks().add(book);
                        shelfFound = true;
                        break;
                    }
}

                // Aktualisiertes Regal speichern
                String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
                properties.setProperty(username, updatedShelvesJson);

                try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
                    properties.store(output, null);
                }
            }
        }
    }

    public void deleteShelf(Shelf shelf, String username) {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        System.out.println("Versuche Regal mit Namen: " + shelf.getName() + " für Benutzer: " + username);

        // Properties-Datei laden
        try (InputStream input = loader.getResourceAsStream(shelfPropertiesFile)) {
            if (input == null) {
                System.out.println("Resource nicht gefunden: " + shelfPropertiesFile);
                return; // Frühzeitiger Rückkehr, wenn die Datei nicht gefunden wurde
            } else {
                properties.load(input);
                System.out.println("Properties geladen");
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Datei: " + shelfPropertiesFile);
            e.printStackTrace();
            return; // Frühzeitiger Rückkehr bei Fehler
        }

        // Überprüfen, ob der Benutzer bereits Regale hat
        String existingShelvesJson = properties.getProperty(username);
        List<Shelf> shelves = new ArrayList<>();

        // Wenn bereits Regale existieren, lade sie in die Liste
        if (existingShelvesJson != null && !existingShelvesJson.trim().isEmpty()) {
            try {
                shelves = objectMapper.readValue(existingShelvesJson, new TypeReference<List<Shelf>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                return; // Frühzeitiger Rückkehr bei Fehler
            }
        }

        System.out.println("Liste der Regale des Benutzers " + username + ": " + shelves);

        // Versuche das Regal anhand des Namens zu finden
        Shelf shelfToRemove = null;
        for (Shelf s : shelves) {
            if (s.getName().equals(shelf.getName())) { // Vergleich anhand des Regalsnamens
                shelfToRemove = s;
                break;
            }
        }

        // Regal entfernen, wenn gefunden
        if (shelfToRemove != null) {
            shelves.remove(shelfToRemove);
            System.out.println("Regal '" + shelf.getName() + "' erfolgreich entfernt");
        } else {
            System.out.println("Regal '" + shelf.getName() + "' nicht gefunden");
            return; // Frühzeitiger Rückkehr, wenn Regal nicht gefunden wurde
        }

        // Speichere die aktualisierte Liste als JSON-Array
        String updatedShelvesJson = null;
        try {
            updatedShelvesJson = objectMapper.writeValueAsString(shelves);
        } catch (IOException e) {
            e.printStackTrace();
            return; // Frühzeitiger Rückkehr bei Fehler
        }

        properties.setProperty(username, updatedShelvesJson);

        // Speichern in die Datei
        try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getAllShelves() {
        Properties properties = new Properties();
        StringBuilder result = new StringBuilder();

        // Versuche die Properties-Datei zu laden
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(shelfPropertiesFile)) {
            if (inputStream == null) {
                throw new IOException("Properties file not found: " + shelfPropertiesFile);
            }

            // Lade die Inhalte der Properties-Datei
            properties.load(inputStream);

            // Iteriere über alle Einträge und hänge sie an den result-String an
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                result.append(key).append("=").append(value).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Rückgabe des gesamten Inhalts als String
        return result.toString();
    }
    public void removeBookFromShelf(String username, String shelfName, Book bookToRemove) {
        Properties properties = new Properties();

        // Laden der Properties-Datei
        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);

            // Bestehende Regale des Benutzers laden
            String shelfData = properties.getProperty(username);
            if (shelfData != null && !shelfData.isEmpty()) {
                List<Shelf> shelves = objectMapper.readValue(shelfData, new TypeReference<List<Shelf>>() {});

                boolean bookRemoved = false; // Flag zur Überprüfung, ob das Buch entfernt wurde

                // Regal finden und Buch entfernen
                for (Shelf shelf : shelves) {
                    if (shelf.getName().equals(shelfName)) {
                        // Vor dem Entfernen prüfen, ob das Buch existiert
                        bookRemoved = shelf.getBooks().removeIf(book ->
                                book.getTitle().equals(bookToRemove.getTitle()) &&
                                        book.getAuthor().equals(bookToRemove.getAuthor()) &&
                                        book.getCoverUrl().equals(bookToRemove.getCoverUrl())
                        );
                        if (bookRemoved) {
                            break; // Buch wurde gefunden und entfernt, Schleife abbrechen
                        }
                    }
                }

                // Wenn das Buch entfernt wurde, speichere die Änderungen
                if (bookRemoved) {
                    String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
                    properties.setProperty(username, updatedShelvesJson);

                    try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
                        properties.store(output, null);
                    }
                } else {
                    // Log-Ausgabe, wenn das Buch nicht gefunden wurde
                    System.err.println("Buch nicht gefunden im Regal: " + shelfName);
                    throw new RuntimeException("Buch konnte nicht gefunden oder entfernt werden");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Properties-Datei nicht gefunden: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen/Schreiben der Properties-Datei: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
