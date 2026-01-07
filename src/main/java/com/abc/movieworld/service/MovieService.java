package com.abc.movieworld.service;

import com.abc.movieworld.dto.MovieDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Movie operations.
 * Provides business logic for CRUD operations on movies and fetches reviews from the review service.
 */
public interface MovieService {

    /**
     * Get all movies.
     * @return List of all movies
     */
    List<MovieDTO> getAllMovies();

    /**
     * Get a movie by its ID.
     * @param id Movie ID
     * @return Optional containing the movie if found, empty otherwise
     */
    Optional<MovieDTO> getMovieById(Long id);

    /**
     * Get a movie by its ID with reviews.
     * @param id Movie ID
     * @return Optional containing the movie with reviews if found, empty otherwise
     */
    Optional<MovieDTO> getMovieWithReviews(Long id);

    /**
     * Create a new movie.
     * @param movieDTO Movie to create
     * @return Created movie
     */
    MovieDTO createMovie(MovieDTO movieDTO);

    /**
     * Update an existing movie.
     * @param id Movie ID
     * @param movieDTO Updated movie details
     * @return Updated movie if found, null otherwise
     */
    MovieDTO updateMovie(Long id, MovieDTO movieDTO);

    /**
     * Delete a movie.
     * @param id Movie ID
     * @return true if deleted, false if not found
     */
    boolean deleteMovie(Long id);
}
