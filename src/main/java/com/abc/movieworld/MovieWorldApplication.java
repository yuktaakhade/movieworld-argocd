package com.abc.movieworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Main application class for the Movie World service.
 * This service provides CRUD operations for movies and communicates with the Movie Review service.
 */
@SpringBootApplication
public class MovieWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieWorldApplication.class, args);
    }
    
    /**
     * WebClient bean for making HTTP requests to other services.
     * Used for communication with the Movie Review service.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
