package com.abc.movieworld.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health check endpoint.
 * Provides an endpoint to check the health of the service.
 */
@RestController
@RequestMapping("/api/movieworld/health")
@Tag(name = "Health Check API", description = "API for health check")
@Slf4j
public class HealthCheckController {

    /**
     * Health check endpoint.
     * @return Health status
     */
    @GetMapping
    @Operation(summary = "Health check", description = "Checks the health of the service")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("REST request for health check");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Movie World Service");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
