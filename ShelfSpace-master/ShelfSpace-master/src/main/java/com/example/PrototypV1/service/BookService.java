package com.example.PrototypV1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BookService {

    @Value("${google.books.api.key}")
    private String apiKey;

    private RestTemplate restTemplate = new RestTemplate();

    public String searchBooksByTitle(String title) throws JsonProcessingException {
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&maxResults=20&key={apiKey}";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));

        return response.getBody();
    }
}
