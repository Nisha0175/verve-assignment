package com.verve.verve_assignment.service;

// import java.util.Set;
// import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.http.ResponseEntity;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RequestService(WebClient webClient, RedisTemplate<String, String> redisTemplate) {
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
    }

    public String processRequest(int id, String endpoint, String method) {
        try {
            // Construct a key for Redis based on the ID to ensure uniqueness
            String redisKey = "unique_request:" + id;
    
            // Check if the ID is already processed (exists in Redis)
            if (redisTemplate.hasKey(redisKey)) {
                return "ok";  // If the ID is already processed, return "ok" (no need to process it again)
            }
    
            // Store the ID in Redis for 1 minute (TTL = 60 seconds)
            redisTemplate.opsForValue().set(redisKey, String.valueOf(id), 180);
    
            if (endpoint != null) {
                // Get the current unique count from Redis (using Long)
                Long uniqueCount = redisTemplate.opsForValue().size(redisKey); // size() returns Long
    
                // You can either keep it as Long or convert to int if necessary
                int uniqueCountInt = uniqueCount != null ? uniqueCount.intValue() : 0;
    
                if ("POST".equalsIgnoreCase(method)) {
                    // Prepare JSON payload for POST request
                    ObjectNode requestBody = objectMapper.createObjectNode();
                    requestBody.put("uniqueCount", uniqueCountInt);
                    requestBody.put("description", "Unique request count in the current minute");
    
                    // Send POST request asynchronously using WebClient
                    webClient.post()
                            .uri(endpoint)
                            .bodyValue(requestBody)
                            .retrieve()
                            .toBodilessEntity()
                            .doOnSuccess(response -> logger.info("POST request response status: {}", response.getStatusCode()))
                            .doOnError(error -> handleRequestError(error))
                            .subscribe();  // Make the request asynchronously
                } else {
                    // Default to GET request if method is not POST
                    String urlWithCount = endpoint + "?count=" + uniqueCountInt;
    
                    // Send GET request asynchronously using WebClient
                    webClient.get()
                            .uri(urlWithCount)
                            .retrieve()
                            .toBodilessEntity()
                            .doOnSuccess(response -> logger.info("GET request response status: {}", response.getStatusCode()))
                            .doOnError(error -> handleRequestError(error))
                            .subscribe();  // Make the request asynchronously
                }
            }
            return "ok";  // Return "ok" if the request is processed successfully
        } catch (Exception e) {
            // Log the error and return "failed" if something goes wrong
            logger.error("Error processing request", e);
            return "failed";
        }
    }

    private void handleRequestError(Throwable error) {
        if (error instanceof WebClientResponseException) {
            // Handle specific WebClient errors (e.g., non-2xx status codes)
            WebClientResponseException webClientError = (WebClientResponseException) error;
            logger.error("HTTP request failed with status: {}", webClientError.getStatusCode(), webClientError);
        } else {
            // Log any other errors that occur during the HTTP request
            logger.error("Unexpected error during HTTP request", error);
        }
    }

    public int getUniqueRequestCount() {
        // Return the number of unique requests received so far
        return redisTemplate.keys("unique_request:*").size();
    }

    public void clearUniqueRequests() {
        // Clear all unique requests from Redis (end of minute or whenever needed)
        redisTemplate.delete(redisTemplate.keys("unique_request:*"));
    }
}