package com.example.PrototypV1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.*;
import java.awt.print.Book;
import java.util.*;
import com.example.PrototypV1.model.SearchResult;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Value("${google.books.api.key}")
    private String apiKey;

    private RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> searchBooksByTitle(String title) throws JsonProcessingException {
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + title + "&key={apiKey}";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, Collections.singletonMap("apiKey", apiKey));
//
//        // Verwende ObjectMapper, um die JSON-Antwort in Book-Objekte umzuwandeln
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = objectMapper.readTree(response.getBody());
//
//        List<SearchResult> books = new ArrayList<>();
//        if (rootNode.has("items")) {
//            for (JsonNode itemNode : rootNode.get("items")) {
//                // Mappe das JSON-Objekt auf ein Book-Objekt
//                SearchResult book = objectMapper.treeToValue(itemNode, SearchResult.class);
//                books.add(book);
//            }
//        }

        return response;
    }
}
