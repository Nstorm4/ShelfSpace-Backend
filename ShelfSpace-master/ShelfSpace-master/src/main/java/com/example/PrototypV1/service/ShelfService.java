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

    public void deleteShelf(String shelfName, String username) {
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
            user.getShelves().remove(shelf);  // Entferne das Regal aus der Liste des Benutzers
            shelfRepository.delete(shelf);    // Lösche das Regal aus der Datenbank
            logger.info("Regal '{}' für Benutzer '{}' erfolgreich gelöscht", shelfName, username);
        } else {
            logger.warn("Regal '{}' für Benutzer '{}' nicht gefunden", shelfName, username);
            throw new IllegalArgumentException("Regal nicht gefunden: " + shelfName);
        }
    }

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
}
