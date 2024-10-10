package com.example.PrototypV1.manager;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class TokenManager {

    private final Map<String, String> userTokens = new HashMap<>();
    private final Map<String, String> tokenToUserMap = new HashMap<>();
    private static final int TOKEN_LENGTH = 32; // Token Länge in Bytes
    private final SecureRandom secureRandom = new SecureRandom();
    private final String propertiesFilePath = "token.properties"; // Pfad zur properties-Datei

    public TokenManager() {
        loadTokensFromProperties();
        System.out.println("Initialisierte tokenToUserMap: " + tokenToUserMap);
    }

    public String generateTokenForUser(String username) {
        String token = generateRandomToken();
        userTokens.put(username, token);
        tokenToUserMap.put(token, username);
        System.out.println("Generierter Token: " + token + " für Benutzer: " + username);
        saveTokensToProperties(); // Speichere die Tokens nach der Erstellung
        return token;
    }

    private String generateRandomToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String getUserForToken(String token) {
        String username = tokenToUserMap.get(token);
        System.out.println("Suchanfrage für Token: " + token + ", Gefundener Benutzer: " + username);
        return username;
    }

    public boolean isTokenValid(String username, String token) {
        return token.equals(userTokens.get(username));
    }

    public void removeTokenForUser(String username) {
        userTokens.remove(username);
        tokenToUserMap.values().remove(username); // Entferne den Token aus der Map
        saveTokensToProperties(); // Speichere die Tokens nach dem Entfernen
        System.out.println("Entfernte Token für Benutzer: " + username);
    }

    public void loadTokensFromProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("token.properties")) {
            if (input == null) {
                throw new FileNotFoundException("token.properties not found in the classpath");
            }
            // Load properties from input stream
            Properties properties = new Properties();
            properties.load(input);
            // Process properties...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void saveTokensToProperties() {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : tokenToUserMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        try (FileOutputStream output = new FileOutputStream(propertiesFilePath)) {
            properties.store(output, "User Tokens");
            System.out.println("Gespeicherte Tokens in die properties-Datei: " + properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
