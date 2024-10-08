package com.example.PrototypV1.service;

import com.example.PrototypV1.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Properties;

@Service
public class UserService {
    @Value("${user.properties.file}")
    private String userPropertiesFile;

    public void register(User user) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            // Wenn die Datei nicht existiert, ist das in Ordnung
        }

        properties.setProperty(user.getUsername(), user.getPassword());

        try (OutputStream output = new FileOutputStream(userPropertiesFile)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(userPropertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            return false;
        }

        String storedPassword = properties.getProperty(username);
        return storedPassword != null && storedPassword.equals(password);
    }
}
