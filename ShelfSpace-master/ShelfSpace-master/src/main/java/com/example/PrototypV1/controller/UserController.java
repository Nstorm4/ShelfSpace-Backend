package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Überprüfen, ob der Benutzer bereits existiert
        if (userService.userExists(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Benutzername existiert bereits");
        }

        // Benutzer registrieren
        userService.register(user);
        return ResponseEntity.ok("Registrierung erfolgreich");
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/test")
    public String getAllUsers() throws IOException {
        return userService.getAllUsers();
    }

}
