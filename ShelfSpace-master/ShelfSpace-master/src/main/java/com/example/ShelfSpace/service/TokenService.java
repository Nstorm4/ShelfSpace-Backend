package com.example.ShelfSpace.service;

import com.example.ShelfSpace.Repository.TokenRepository;
import com.example.ShelfSpace.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
       this.tokenRepository = tokenRepository;
    }

    public String generateTokenForUser(String username) {
        logger.info("Generating token for user: {}", username);
        String token = generateRandomToken();
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUsername(username);

        try {
            tokenRepository.save(tokenEntity);
            logger.info("Token successfully generated and saved for user: {}", username);
        } catch (Exception e) {
            logger.error("Error saving token for user: {}", username, e);
        }

        return token;
    }

    private String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        logger.debug("Random token generated: {}", token);
        return token;
    }

    public String getUserForToken(String token) {
        logger.info("Fetching user for token: {}", token);

        return tokenRepository.findByToken(token)
                .map(tokenEntity -> {
                    logger.info("User {} found for token", tokenEntity.getUsername());
                    return tokenEntity.getUsername();
                })
                .orElseGet(() -> {
                    logger.warn("No user found for token: {}", token);
                    return null;
                });
    }

    public void removeToken(String token) {
        logger.info("Removing token: {}", token);

        try {
            tokenRepository.deleteById(token);
            logger.info("Token {} successfully removed", token);
        } catch (Exception e) {
            logger.error("Error removing token: {}", token, e);
        }
    }
}
