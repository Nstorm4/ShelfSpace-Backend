package com.example.PrototypV1.service;

import com.example.PrototypV1.manager.TokenManager;
import com.example.PrototypV1.model.User;
import com.example.PrototypV1.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenManager tokenManager = new TokenManager();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Benutzer registrieren
    public void register(User user) {
        if (userRepository.existsById(user.getUsername())) {
            logger.warn("Benutzer {} existiert bereits", user.getUsername());
            throw new IllegalArgumentException("Benutzername ist bereits vergeben");
        }

        userRepository.save(user);
        logger.info("Benutzer {} erfolgreich registriert", user.getUsername());
    }

    // Benutzer-Login
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            String token = tokenManager.generateTokenForUser(username);
            logger.info("Login erfolgreich für Benutzer: {}", username);
            return token;
        }

        logger.warn("Login fehlgeschlagen: Ungültige Anmeldeinformationen für Benutzer: {}", username);
        return null;
    }

    // Überprüfen, ob der Benutzer existiert
    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

    // Alle Benutzer abrufen
    public String getAllUsers() {
        List<User> users = userRepository.findAll();
        StringBuilder result = new StringBuilder();

        for (User user : users) {
            result.append(user.getUsername()).append("\n");
        }

        logger.info("Benutzerliste erfolgreich abgerufen");
        return result.toString();
    }

    // Benutzer-Logout
    public void logout(String token) {
        tokenManager.removeToken(token);
        logger.info("Benutzer mit Token {} erfolgreich ausgeloggt", token);
    }
}
