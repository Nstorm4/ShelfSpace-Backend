package com.example.PrototypV1.service;

import com.example.PrototypV1.model.Book;
import com.example.PrototypV1.model.Shelf;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ShelfService {

    private static final Logger logger = LoggerFactory.getLogger(ShelfService.class); // SLF4J Logger

    @Value("${shelf.properties.file}")
    private String shelfPropertiesFile;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    /**
     * Speichert ein neues Regal für den Benutzer.
     */
    public void createShelf(Shelf shelf, String username) throws IOException {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        logger.info("Versuche, ein neues Regal für Benutzer '{}' zu erstellen", username);
        try (InputStream input = loader.getResourceAsStream(shelfPropertiesFile)) {
            if (input == null) {
                logger.error("Properties-Datei '{}' nicht gefunden", shelfPropertiesFile);
                throw new FileNotFoundException("Resource nicht gefunden: " + shelfPropertiesFile);
            }
            properties.load(input);
            logger.debug("Properties-Datei geladen: {}", shelfPropertiesFile);
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Datei: {}", shelfPropertiesFile, e);
            throw e; // Fehler wird weitergeleitet, um vom Aufrufer behandelt zu werden
        }

        String existingShelvesJson = properties.getProperty(username);
        List<Shelf> shelves = new ArrayList<>();

        if (existingShelvesJson != null && !existingShelvesJson.trim().isEmpty()) {
            try {
                shelves = objectMapper.readValue(existingShelvesJson, new TypeReference<List<Shelf>>() {});
                logger.debug("Vorhandene Regale für Benutzer '{}' geladen", username);
            } catch (JsonProcessingException e) {
                logger.error("Fehler beim Parsen der Shelf-Daten für Benutzer '{}'", username, e);
                throw e; // Fehler beim Verarbeiten von JSON
            }
        }

        shelves.add(shelf);
        String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
        properties.setProperty(username, updatedShelvesJson);

        try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
            properties.store(output, null);
            logger.info("Regal für Benutzer '{}' erfolgreich erstellt", username);
        } catch (IOException e) {
            logger.error("Fehler beim Speichern der Shelf-Daten in der Datei '{}'", shelfPropertiesFile, e);
            throw e;
        }
    }

    /**
     * Lädt alle Regale eines Benutzers aus der Properties-Datei.
     */
    public List<Shelf> getShelvesByUsername(String username) {
        Properties properties = new Properties();
        List<Shelf> shelves = new ArrayList<>();

        logger.info("Lade Regale für Benutzer '{}'", username);
        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);
            String shelfData = properties.getProperty(username);
            if (shelfData != null && !shelfData.isEmpty()) {
                try {
                    shelves = objectMapper.readValue(shelfData, new TypeReference<List<Shelf>>() {});
                    logger.debug("Regale für Benutzer '{}' erfolgreich geladen", username);
                } catch (JsonProcessingException e) {
                    logger.error("Fehler beim Konvertieren der Shelf-Daten für Benutzer '{}'", username, e);
                }
            }
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Datei '{}'", shelfPropertiesFile, e);
        }

        return shelves;
    }

    /**
     * Fügt ein Buch zu einem Regal hinzu.
     */
    public void addBookToShelf(String username, String shelfName, Book book) throws IOException {
        Properties properties = new Properties();
        logger.info("Füge Buch '{}' zu Regal '{}' für Benutzer '{}' hinzu", book.getTitle(), shelfName, username);

        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);

            String shelfData = properties.getProperty(username);
            if (shelfData != null && !shelfData.isEmpty()) {
                List<Shelf> shelves = objectMapper.readValue(shelfData, new TypeReference<List<Shelf>>() {});

                boolean shelfFound = false;
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

                if (!shelfFound) {
                    logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelfName, username);
                    throw new IllegalArgumentException("Regal nicht gefunden: " + shelfName);
                }

                String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
                properties.setProperty(username, updatedShelvesJson);

                try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
                    properties.store(output, null);
                    logger.info("Buch '{}' erfolgreich zum Regal '{}' hinzugefügt", book.getTitle(), shelfName);
                }
            } else {
                logger.warn("Keine Regale für Benutzer '{}' gefunden", username);
                throw new IllegalArgumentException("Keine Regale für Benutzer gefunden: " + username);
            }
        } catch (IOException e) {
            logger.error("Fehler beim Hinzufügen des Buches zum Regal", e);
            throw e;
        }
    }

    /**
     * Löscht ein Regal eines Benutzers.
     */
    public void deleteShelf(Shelf shelf, String username) {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        logger.info("Versuche, das Regal '{}' für Benutzer '{}' zu löschen", shelf.getName(), username);

        try (InputStream input = loader.getResourceAsStream(shelfPropertiesFile)) {
            if (input == null) {
                logger.error("Properties-Datei '{}' nicht gefunden", shelfPropertiesFile);
                throw new FileNotFoundException("Resource nicht gefunden: " + shelfPropertiesFile);
            }
            properties.load(input);

            String existingShelvesJson = properties.getProperty(username);
            List<Shelf> shelves = new ArrayList<>();

            if (existingShelvesJson != null && !existingShelvesJson.trim().isEmpty()) {
                shelves = objectMapper.readValue(existingShelvesJson, new TypeReference<List<Shelf>>() {});
            }

            Shelf shelfToRemove = shelves.stream()
                    .filter(s -> s.getName().equals(shelf.getName()))
                    .findFirst()
                    .orElse(null);

            if (shelfToRemove != null) {
                shelves.remove(shelfToRemove);
                logger.info("Regal '{}' erfolgreich entfernt", shelf.getName());

                String updatedShelvesJson = objectMapper.writeValueAsString(shelves);
                properties.setProperty(username, updatedShelvesJson);

                try (OutputStream output = new FileOutputStream(shelfPropertiesFile)) {
                    properties.store(output, null);
                }
            } else {
                logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelf.getName(), username);
                throw new IllegalArgumentException("Regal nicht gefunden: " + shelf.getName());
            }
        } catch (IOException e) {
            logger.error("Fehler beim Löschen des Regals '{}'", shelf.getName(), e);
        }
    }
    public String getAllShelves() {
        Properties properties = new Properties();
        StringBuilder result = new StringBuilder();

        logger.info("Versuche, alle Regale abzurufen.");

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(shelfPropertiesFile)) {
            if (inputStream == null) {
                logger.error("Properties-Datei '{}' nicht gefunden.", shelfPropertiesFile);
                throw new IOException("Properties file not found: " + shelfPropertiesFile);
            }

            properties.load(inputStream);
            logger.debug("Properties-Datei '{}' erfolgreich geladen.", shelfPropertiesFile);

            // Iteriere über alle Einträge und hänge sie an den result-String an
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                result.append(key).append("=").append(value).append("\n");
            }
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Properties-Datei: {}", e.getMessage(), e);
            throw new RuntimeException("Fehler beim Abrufen der Regale", e);
        }

        // Rückgabe des gesamten Inhalts als String
        logger.info("Alle Regale erfolgreich abgerufen.");
        return result.toString();
    }

    /**
     * Entfernt ein Buch aus dem angegebenen Regal eines Benutzers.
     */
    public void removeBookFromShelf(String username, String shelfName, Book bookToRemove) {
        Properties properties = new Properties();
        logger.info("Versuche, Buch '{}' aus Regal '{}' für Benutzer '{}' zu entfernen", bookToRemove.getTitle(), shelfName, username);

        try (InputStream input = new FileInputStream(shelfPropertiesFile)) {
            properties.load(input);
            logger.debug("Properties-Datei '{}' erfolgreich geladen.", shelfPropertiesFile);

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
                            logger.info("Buch '{}' erfolgreich aus Regal '{}' entfernt.", bookToRemove.getTitle(), shelfName);
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
                        logger.info("Änderungen erfolgreich in der Properties-Datei gespeichert.");
                    }
                } else {
                    logger.warn("Buch '{}' nicht gefunden im Regal '{}'", bookToRemove.getTitle(), shelfName);
                    throw new RuntimeException("Buch konnte nicht gefunden oder entfernt werden");
                }
            } else {
                logger.warn("Keine Regale gefunden für Benutzer '{}'", username);
                throw new RuntimeException("Keine Regale für Benutzer gefunden: " + username);
            }
        } catch (FileNotFoundException e) {
            logger.error("Properties-Datei nicht gefunden: {}", e.getMessage());
            throw new RuntimeException("Properties-Datei nicht gefunden", e);
        } catch (IOException e) {
            logger.error("Fehler beim Lesen/Schreiben der Properties-Datei: {}", e.getMessage());
            throw new RuntimeException("Fehler beim Entfernen des Buches", e);
        }
    }
}
