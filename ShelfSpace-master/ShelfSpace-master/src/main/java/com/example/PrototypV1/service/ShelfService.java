package com.example.PrototypV1.service;

import com.example.PrototypV1.model.Book;
import com.example.PrototypV1.model.Shelf;
import com.example.PrototypV1.model.User;
import com.example.PrototypV1.Repository.ShelfRepository;
import com.example.PrototypV1.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {

    private static final Logger logger = LoggerFactory.getLogger(ShelfService.class);

    private final ShelfRepository shelfRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShelfService(ShelfRepository shelfRepository, UserRepository userRepository) {
        this.shelfRepository = shelfRepository;
        this.userRepository = userRepository;
    }

    /**
     * Erstellt ein neues Regal für einen Benutzer basierend auf dem Benutzernamen.
     */
    public void createShelf(Shelf shelf, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Benutzer '{}' nicht gefunden", username);
            throw new IllegalArgumentException("Benutzer nicht gefunden");
        }

        shelf.setUser(user);
        shelfRepository.save(shelf);
        logger.info("Regal '{}' für Benutzer '{}' erfolgreich erstellt", shelf.getName(), username);
    }

    /**
     * Lädt alle Regale eines Benutzers.
     */
    public List<Shelf> getShelvesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Benutzer '{}' nicht gefunden", username);
            throw new IllegalArgumentException("Benutzer nicht gefunden");
        }

        List<Shelf> shelves = user.getShelves();
        logger.info("Regale für Benutzer '{}' erfolgreich geladen", username);
        return shelves;
    }

    /**
     * Fügt ein Buch zu einem Regal hinzu.
     */
    public void addBookToShelf(String username, String shelfName, Book book) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Benutzer '{}' nicht gefunden", username);
            throw new IllegalArgumentException("Benutzer nicht gefunden");
        }

        Optional<Shelf> shelfOptional = user.getShelves().stream()
                .filter(shelf -> shelf.getName().equals(shelfName))
                .findFirst();

        if (shelfOptional.isPresent()) {
            Shelf shelf = shelfOptional.get();
            shelf.getBooks().add(book);
            shelfRepository.save(shelf);
            logger.info("Buch '{}' erfolgreich zu Regal '{}' hinzugefügt", book.getTitle(), shelfName);
        } else {
            logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelfName, username);
            throw new IllegalArgumentException("Regal nicht gefunden: " + shelfName);
        }
    }

    /**
     * Löscht ein Regal eines Benutzers basierend auf dem Benutzernamen und Regalnamen.
     */
    public void deleteShelf(String shelfName, String username) {
        User user = userRepository.findByUsername(username);
        System.out.println("I am in the ShelfService deleteShelf and this is the user:" + user);
        if (user == null) {
            logger.warn("Benutzer '{}' nicht gefunden", username);
            throw new IllegalArgumentException("Benutzer nicht gefunden");
        }

        Optional<Shelf> shelfOptional = user.getShelves().stream()
                .filter(shelf -> shelf.getName().equals(shelfName))
                .findFirst();
        System.out.println("I am in the Service, I found the shelf: " + shelfOptional.toString());

        if (shelfOptional.isPresent()) {
            Shelf shelf = shelfOptional.get();
            user.getShelves().remove(shelf);  // Entferne das Regal aus der Liste des Benutzers
            shelfRepository.delete(shelf);    // Lösche das Regal aus der Datenbank
            logger.info("Regal '{}' für Benutzer '{}' erfolgreich gelöscht", shelfName, username);
        } else {
            logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelfName, username);
            throw new IllegalArgumentException("Regal nicht gefunden: " + shelfName);
        }
    }

    /**
     * Entfernt ein Buch aus einem Regal basierend auf dem Benutzernamen und Regalnamen.
     */
    public void removeBookFromShelf(String username, String shelfName, Book bookToRemove) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Benutzer '{}' nicht gefunden", username);
            throw new IllegalArgumentException("Benutzer nicht gefunden");
        }

        Optional<Shelf> shelfOptional = user.getShelves().stream()
                .filter(shelf -> shelf.getName().equals(shelfName))
                .findFirst();

        if (shelfOptional.isPresent()) {
            Shelf shelf = shelfOptional.get();
            boolean removed = shelf.getBooks().removeIf(book ->
                    book.getTitle().equals(bookToRemove.getTitle()) &&
                            book.getAuthor().equals(bookToRemove.getAuthor()) &&
                            book.getCoverUrl().equals(bookToRemove.getCoverUrl())
            );

            if (removed) {
                shelfRepository.save(shelf);
                logger.info("Buch '{}' erfolgreich aus Regal '{}' entfernt", bookToRemove.getTitle(), shelfName);
            } else {
                logger.warn("Buch '{}' nicht im Regal '{}' gefunden", bookToRemove.getTitle(), shelfName);
                throw new IllegalArgumentException("Buch nicht gefunden");
            }
        } else {
            logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelfName, username);
            throw new IllegalArgumentException("Regal nicht gefunden");
        }
    }

    /**
     * Gibt alle Regale aller Benutzer als String zurück, einschließlich der Bücher und Nutzerinformationen.
     */
    public String getAllShelves() {
        StringBuilder result = new StringBuilder();
        List<Shelf> shelves = shelfRepository.findAll(); // Alle Regale aus der Datenbank abrufen

        logger.info("Versuche, alle Regale abzurufen.");

        for (Shelf shelf : shelves) {
            result.append("Regal: ").append(shelf.getName()).append("\n");
            result.append("Benutzer: ").append(shelf.getUser().getUsername()).append("\n"); // Benutzername aus dem Regal

            if (shelf.getBooks() != null && !shelf.getBooks().isEmpty()) {
                result.append("Bücher:\n");
                for (Book book : shelf.getBooks()) {
                    result.append("- ").append(book.getTitle()).append(" von ").append(book.getAuthor()).append("\n");
                }
            } else {
                result.append("Keine Bücher in diesem Regal.\n");
            }
            result.append("\n"); // Leerzeile zwischen den Regalen
        }

        logger.info("Alle Regale erfolgreich abgerufen.");
        return result.toString();
    }

}
