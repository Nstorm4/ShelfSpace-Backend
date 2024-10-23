package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.User;
import com.example.PrototypV1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // Logger hinzufügen

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Überprüfen, ob der Benutzer bereits existiert
        if (userService.userExists(user.getUsername())) {
            logger.warn("Registrierung fehlgeschlagen: Benutzername {} existiert bereits", user.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Benutzername existiert bereits");
        }

        // Benutzer registrieren
        userService.register(user);
        logger.info("Benutzer {} erfolgreich registriert", user.getUsername());
        return ResponseEntity.ok("Registrierung erfolgreich");
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if (token == null) {
            logger.warn("Login fehlgeschlagen für Benutzer: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ungültige Anmeldeinformationen");
        }

        logger.info("Login erfolgreich für Benutzer: {}", user.getUsername());
        return ResponseEntity.ok(token);
    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @GetMapping("/test")
    public ResponseEntity<String> getAllUsers() {
            String users = userService.getAllUsers();
            logger.info("Alle Benutzer erfolgreich abgerufen");
            return ResponseEntity.ok(users);

    }

    // @CrossOrigin(origins = "http://shelfspace-react.apps.01.cf.eu01.stackit.cloud")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && !token.isEmpty()) {
            userService.logout(token);
            logger.info("Benutzer mit Token {} erfolgreich ausgeloggt", token);
            return ResponseEntity.ok("Logout erfolgreich");
        }

        logger.warn("Logout fehlgeschlagen: Ungültiger Token");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ungültiger Token");
    }
}
