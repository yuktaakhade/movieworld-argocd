package com.abc.movieworld.service;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.mapper.MovieMapper;
import com.abc.movieworld.model.Movie;
import com.abc.movieworld.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation class for Movie operations.
 * Provides business logic for CRUD operations on movies and fetches reviews from the review service.
 */
@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final WebClient.Builder webClientBuilder;
    private final MovieMapper movieMapper;
    
    @Value("${moviereview.service.url}")
    private String movieReviewServiceUrl;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, WebClient.Builder webClientBuilder, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.webClientBuilder = webClientBuilder;
        this.movieMapper = movieMapper;
    }

    /**
     * Get all movies.
     * @return List of all movies
     */
    @Override
    public List<MovieDTO> getAllMovies() {
        log.info("Fetching all movies");
        List<Movie> movies = movieRepository.findAll();
        return movieMapper.toDTOList(movies);
    }

    /**
     * Get a movie by its ID.
     * @param id Movie ID
     * @return Optional containing the movie if found, empty otherwise
     */
    @Override
    public Optional<MovieDTO> getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        return movieRepository.findById(id)
                .map(movieMapper::toDTO);
    }

    /**
     * Get a movie by its ID with reviews.
     * @param id Movie ID
     * @return Optional containing the movie with reviews if found, empty otherwise
     */
    @Override
    public Optional<MovieDTO> getMovieWithReviews(Long id) {
        log.info("Fetching movie with id: {} including reviews", id);
        Optional<Movie> movieOpt = movieRepository.findById(id);
        
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            // Fetch reviews from the review service
            try {
                Object reviews = webClientBuilder.build()
                        .get()
                        .uri(movieReviewServiceUrl + "/api/moviereview/movie/{id}", id)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .onErrorResume(e -> {
                            log.error("Error fetching reviews for movie id: {}", id, e);
                            return Mono.empty();
                        })
                        .block();
                
                movie.setReviews(reviews);
            } catch (Exception e) {
                log.error("Failed to fetch reviews for movie id: {}", id, e);
            }
            return Optional.of(movieMapper.toDTO(movie));
        }
        
        return Optional.empty();
    }

    /**
     * Create a new movie.
     * @param movieDTO Movie to create
     * @return Created movie
     */
    @Override
    public MovieDTO createMovie(MovieDTO movieDTO) {
        log.info("Creating new movie: {}", movieDTO);
        Movie movie = movieMapper.toEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toDTO(savedMovie);
    }

    /**
     * Update an existing movie.
     * @param id Movie ID
     * @param movieDTO Updated movie details
     * @return Updated movie if found, null otherwise
     */
    @Override
    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        log.info("Updating movie with id: {}", id);
        Optional<Movie> movieOpt = movieRepository.findById(id);
        
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            movieMapper.updateEntityFromDTO(movie, movieDTO);
            Movie updatedMovie = movieRepository.save(movie);
            return movieMapper.toDTO(updatedMovie);
        }
        
        return null;
    }

    /**
     * Delete a movie.
     * @param id Movie ID
     * @return true if deleted, false if not found
     */
    @Override
    public boolean deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
