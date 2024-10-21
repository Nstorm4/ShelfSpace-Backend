package com.example.PrototypV1.service;

import com.example.PrototypV1.manager.TokenManager;
import com.example.PrototypV1.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

@Service
public class UserService {

    @Value("${user.properties.file}")
    private String userPropertiesFile;
    private TokenManager tokenManager = new TokenManager();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // Logger hinzufügen

    public void register(User user) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            logger.warn("Benutzer-Properties-Datei nicht gefunden. Erstelle eine neue Datei: {}", userPropertiesFile);
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Benutzer-Properties-Datei: {}", userPropertiesFile, e);
            return;
        }

        properties.setProperty(user.getUsername(), user.getPassword());

        try (OutputStream output = new FileOutputStream(userPropertiesFile)) {
            properties.store(output, null);
            logger.info("Benutzer {} erfolgreich registriert", user.getUsername());
        } catch (IOException e) {
            logger.error("Fehler beim Speichern der Benutzer-Properties-Datei: {}", userPropertiesFile, e);
        }
    }

    public String login(String username, String password) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Benutzer-Properties-Datei: {}", userPropertiesFile, e);
            return null;
        }

        String storedPassword = properties.getProperty(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            String token = tokenManager.generateTokenForUser(username);
            logger.info("Login erfolgreich für Benutzer: {}", username);
            return token;
        }

        logger.warn("Login fehlgeschlagen: Ungültige Anmeldeinformationen für Benutzer: {}", username);
        return null;
    }

    public boolean userExists(String username) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Fehler beim Überprüfen, ob Benutzer existiert: {}", username, e);
            return false;
        }

        return properties.getProperty(username) != null;
    }

    public String getAllUsers() throws IOException {
        Properties properties = new Properties();
        StringBuilder result = new StringBuilder();

        // Versuche die Properties-Datei zu laden
        try (InputStream inputStream = new FileInputStream(userPropertiesFile)) {
            if (inputStream == null) {
                logger.error("Properties-Datei nicht gefunden: {}", userPropertiesFile);
                throw new IOException("Properties file not found: " + userPropertiesFile);
            }

            // Lade die Inhalte der Properties-Datei
            properties.load(inputStream);

            // Iteriere über alle Einträge und hänge sie an den result-String an
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                result.append(key).append("=").append(value).append("\n");
            }
        } catch (IOException e) {
            logger.error("Fehler beim Laden der Benutzerliste aus der Datei: {}", userPropertiesFile, e);
            throw e;
        }

        logger.info("Benutzerliste erfolgreich abgerufen");
        return result.toString();
    }

    public void logout(String token) {
        tokenManager.removeToken(token);
        logger.info("Benutzer mit Token {} erfolgreich ausgeloggt", token);
    }
}
