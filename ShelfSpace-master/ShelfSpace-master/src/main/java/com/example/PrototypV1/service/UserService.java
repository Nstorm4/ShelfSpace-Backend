package com.example.PrototypV1.service;

import com.example.PrototypV1.manager.*;
import com.example.PrototypV1.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Properties;

@Service
public class UserService {
    @Value("${user.properties.file}")
    private String userPropertiesFile;
    private TokenManager tokenManager = new TokenManager();

    public void register(User user) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
        }

        properties.setProperty(user.getUsername(), user.getPassword());

        try (OutputStream output = new FileOutputStream(userPropertiesFile)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String login(String username, String password) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            return "Fehler beim Laden der Login aufgetret";
        }

        String storedPassword = properties.getProperty(username);
        if (storedPassword != null && storedPassword.equals(password))
        {
            return tokenManager.generateTokenForUser(properties.getProperty(username));
        }
        return "Fehler beim Laden der Login aufgetret";
    }
    public boolean userExists(String username) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            return false;
        }

        return properties.getProperty(username) != null;
    }
        // Methode um alle User aus dem properties File als String oder JSON zurückzugeben
        public String getAllUsers() throws IOException {
            Properties properties = new Properties();
            StringBuilder result = new StringBuilder();

            // Versuche die Properties-Datei zu laden
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(userPropertiesFile)) {
                if (inputStream == null) {
                    throw new IOException("Properties file not found: " + userPropertiesFile);
                }

                // Lade die Inhalte der Properties-Datei
                properties.load(inputStream);

                // Iteriere über alle Einträge und hänge sie an den result-String an
                for (String key : properties.stringPropertyNames()) {
                    String value = properties.getProperty(key);
                    result.append(key).append("=").append(value).append("\n");
                }
            }

            // Rückgabe des gesamten Inhalts als String
            return result.toString();
        }
    }
