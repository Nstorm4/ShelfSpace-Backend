package com.example.PrototypV1.manager;

import com.example.PrototypV1.model.User;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class TokenManager {

    private static final int TOKEN_LENGTH = 32; // Token Länge in Bytes
    private final SecureRandom secureRandom = new SecureRandom();
    private final String propertiesFilePath = "token.properties"; // Pfad zur properties-Datei

    public TokenManager() {
//        loadTokensFromProperties();
//        System.out.println("Initialisierte tokenToUserMap: " + tokenToUserMap);
    }

    public String generateTokenForUser(String username) {
        String token = generateRandomToken();
        System.out.println("Generierter Token: " + token + " für Benutzer: " + username);
        saveTokensToProperties(username, token); // Speichere die Tokens nach der Erstellung

        return token;
    }

    private String generateRandomToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

//    public String getUserForToken(String token) {
//        String username = tokenToUserMap.get(token);
//        System.out.println("Suchanfrage für Token: " + token + ", Gefundener Benutzer: " + username);
//        return username;
//    }
    public String getUserForToken(String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            return null;
        }
        return properties.getProperty(token);
    }

//    private void saveTokensToProperties() {
//        Properties properties = new Properties();
//        for (Map.Entry<String, String> entry : tokenToUserMap.entrySet()) {
//            properties.setProperty(entry.getKey(), entry.getValue());
//        }
//        try (FileOutputStream output = new FileOutputStream(propertiesFilePath)) {
//            properties.store(output, "User Tokens");
//            System.out.println("Gespeicherte Tokens in die properties-Datei: " + properties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void saveTokensToProperties(String username, String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException e) {
        }
System.out.println("saveTokensToProperties:\n" + username);
        properties.setProperty(token, username);

        try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
