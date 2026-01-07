package com.abc.movieworld.controller;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.service.MovieService;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for Movie operations.
 * Provides endpoints for CRUD operations on movies.
 */
@RestController
@RequestMapping("/api/movieworld")
@Tag(name = "Movie API", description = "API for movie operations")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Get all movies.
     * @return List of all movies
     */
    @GetMapping
    @Operation(summary = "Get all movies", description = "Returns a list of all movies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved movies")
    @Observed(name = "movie.getAll", 
             contextualName = "get-all-movies", 
             lowCardinalityKeyValues = {"service", "movie-world"})
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        log.info("REST request to get all movies");
        List<MovieDTO> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    /**
     * Get a movie by its ID.
     * @param id Movie ID
     * @return Movie if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a movie by ID", description = "Returns a movie by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved movie"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        log.info("REST request to get movie with id: {}", id);
        Optional<MovieDTO> movie = movieService.getMovieById(id);
        return movie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a movie by its ID with reviews.
     * @param id Movie ID
     * @return Movie with reviews if found
     */
    @GetMapping("/{id}/with-reviews")
    @Operation(summary = "Get a movie by ID with reviews", description = "Returns a movie by its ID including reviews")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved movie with reviews"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieDTO> getMovieWithReviews(@PathVariable Long id) {
        log.info("REST request to get movie with id: {} including reviews", id);
        Optional<MovieDTO> movie = movieService.getMovieWithReviews(id);
        return movie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new movie.
     * @param movieDTO Movie to create
     * @return Created movie
     */
    @PostMapping
    @Operation(summary = "Create a new movie", description = "Creates a new movie")
    @ApiResponse(responseCode = "201", description = "Movie created successfully")
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO) {
        log.info("REST request to create a new movie: {}", movieDTO);
        MovieDTO createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    /**
     * Update an existing movie.
     * @param id Movie ID
     * @param movieDTO Updated movie details
     * @return Updated movie if found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a movie", description = "Updates an existing movie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDTO) {
        log.info("REST request to update movie with id: {}", id);
        MovieDTO updatedMovie = movieService.updateMovie(id, movieDTO);
        return updatedMovie != null ? 
                ResponseEntity.ok(updatedMovie) : 
                ResponseEntity.notFound().build();
    }

    /**
     * Delete a movie.
     * @param id Movie ID
     * @return No content if deleted
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("REST request to delete movie with id: {}", id);
        boolean deleted = movieService.deleteMovie(id);
        return deleted ? 
                ResponseEntity.noContent().build() : 
                ResponseEntity.notFound().build();
    }
}
