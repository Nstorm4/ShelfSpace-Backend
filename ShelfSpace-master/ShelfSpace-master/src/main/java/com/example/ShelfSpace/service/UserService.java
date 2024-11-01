package com.example.ShelfSpace.service;

import com.example.ShelfSpace.model.User;
import com.example.ShelfSpace.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public void register(User user) {
        if (userRepository.existsById(user.getUsername())) {
            logger.warn("Benutzer {} existiert bereits", user.getUsername());
            throw new IllegalArgumentException("Benutzername ist bereits vergeben");
        }

        userRepository.save(user);
        logger.info("Benutzer {} erfolgreich registriert", user.getUsername());
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            String token = tokenService.generateTokenForUser(username);
            logger.info("Login erfolgreich für Benutzer: {}", username);
            return token;
        }

        logger.warn("Login fehlgeschlagen: Ungültige Anmeldeinformationen für Benutzer: {}", username);
        return null;
    }

    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }
}
