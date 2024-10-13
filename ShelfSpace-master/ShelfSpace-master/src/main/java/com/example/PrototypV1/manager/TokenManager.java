package com.example.PrototypV1.manager;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Component
public class TokenManager {

    private static final int TOKEN_LENGTH = 32; // Token Länge in Bytes
    private final SecureRandom secureRandom = new SecureRandom();
    private final String propertiesFilePath = "token.properties";

    public TokenManager() {
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

    public String getUserForToken(String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            return null;
        }
        return properties.getProperty(token);
    }

    private void saveTokensToProperties(String username, String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.setProperty(token, username);

        try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void removeToken(String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Entferne den Token, falls er existiert
        if (properties.containsKey(token)) {
            properties.remove(token);

            // Speichere die aktualisierte Datei
            try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
                properties.store(output, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
