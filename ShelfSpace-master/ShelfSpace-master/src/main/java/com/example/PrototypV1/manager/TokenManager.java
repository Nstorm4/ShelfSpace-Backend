package com.example.PrototypV1.manager;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {

    private final Map<String, String> userTokens = new HashMap<>();
    private static final int TOKEN_LENGTH = 32; // Token Länge in Bytes
    private final SecureRandom secureRandom = new SecureRandom();

    // Methode zum Generieren eines zufälligen Tokens und Zuweisen zu einem Benutzernamen
    public String generateTokenForUser(String username) {
        String token = generateRandomToken();
        userTokens.put(username, token);
        return token;
    }

    // Methode zum Generieren eines zufälligen Tokens
    private String generateRandomToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    // Methode, um das Token für einen Benutzer abzurufen
    public String getTokenForUser(String username) {
        return userTokens.get(username);
    }

    public String getUserForToken(String token) {
        return userTokens.get(token);
    }

    // Methode, um zu prüfen, ob ein Token existiert
    public boolean isTokenValid(String username, String token) {
        return token.equals(userTokens.get(username));
    }

    // Methode, um ein Token für einen Benutzer zu entfernen (Abmelden)
    public void removeTokenForUser(String username) {
        userTokens.remove(username);
    }
}
