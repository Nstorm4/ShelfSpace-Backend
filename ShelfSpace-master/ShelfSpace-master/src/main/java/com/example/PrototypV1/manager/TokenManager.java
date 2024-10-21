package com.example.PrototypV1.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Component
public class TokenManager {

    private static final int TOKEN_LENGTH = 32; // Token Länge in Bytes
    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class); // Logger initialisieren
    private final SecureRandom secureRandom = new SecureRandom();
    private final String propertiesFilePath = "token.properties";

    public TokenManager() {
    }

    public String generateTokenForUser(String username) {
        String token = generateRandomToken();
        logger.info("Generated token for user: {}", username);
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
        } catch (FileNotFoundException e) {
            logger.error("Token properties file not found: {}", propertiesFilePath, e);
            return null;  // oder Optional.empty() für bessere Handhabung
        } catch (IOException e) {
            logger.error("Error reading token properties file: {}", propertiesFilePath, e);
            return null;
        }
        return properties.getProperty(token);
    }

    private void saveTokensToProperties(String username, String token) {
        Properties properties = new Properties();
        // Lädt bestehende Tokens
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            logger.warn("Token properties file not found. Creating a new one: {}", propertiesFilePath);
            // Es könnte in Ordnung sein, eine neue Datei zu erstellen, daher keine Rückgabe.
        } catch (IOException e) {
            logger.error("Error loading token properties file: {}", propertiesFilePath, e);
            return;
        }

        // Speichert das neue Token
        properties.setProperty(token, username);

        try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
            properties.store(output, null);
            logger.info("Token saved for user: {}", username);
        } catch (IOException e) {
            logger.error("Error saving token to properties file: {}", propertiesFilePath, e);
        }
    }

    public void removeToken(String token) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            logger.error("Token properties file not found: {}", propertiesFilePath, e);
            return;
        } catch (IOException e) {
            logger.error("Error reading token properties file: {}", propertiesFilePath, e);
            return;
        }

        // Entferne den Token, falls er existiert
        if (properties.containsKey(token)) {
            properties.remove(token);

            // Speichere die aktualisierte Datei
            try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
                properties.store(output, null);
                logger.info("Token {} removed successfully.", token);
            } catch (IOException e) {
                logger.error("Error saving properties file after token removal: {}", propertiesFilePath, e);
            }
        } else {
            logger.warn("Token {} not found, nothing to remove.", token);
        }
    }
}
