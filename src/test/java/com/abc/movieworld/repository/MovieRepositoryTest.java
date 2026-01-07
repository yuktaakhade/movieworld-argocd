package com.abc.movieworld.repository;

import com.abc.movieworld.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findById_WithExistingId_ShouldReturnMovie() {
        // Given
        Movie movie = new Movie(null, "Test Movie", "Test Director", LocalDate.of(2023, 1, 1), 120, "Action", null, "Test movie description", null);
        entityManager.persist(movie);
        entityManager.flush();

        // When
        Optional<Movie> found = movieRepository.findById(movie.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test Movie", found.get().getTitle());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // When
        Optional<Movie> found = movieRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllMovies() {
        // Given
        Movie movie1 = new Movie(null, "Test Movie 1", "Test Director 1", LocalDate.of(2023, 1, 1), 120, "Action", null, "Test movie description 1", null);
        Movie movie2 = new Movie(null, "Test Movie 2", "Test Director 2", LocalDate.of(2023, 2, 1), 130, "Drama", null, "Test movie description 2", null);
        entityManager.persist(movie1);
        entityManager.persist(movie2);
        entityManager.flush();

        // When
        List<Movie> movies = movieRepository.findAll();

        // Then
        assertEquals(2, movies.size());
    }

    @Test
    void save_ShouldPersistMovie() {
        // Given
        Movie movie = new Movie(null, "Test Movie", "Test Director", LocalDate.of(2023, 1, 1), 120, "Action", null, "Test movie description", null);

        // When
        Movie saved = movieRepository.save(movie);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test Movie", saved.getTitle());
    }

    @Test
    void delete_ShouldRemoveMovie() {
        // Given
        Movie movie = new Movie(null, "Test Movie", "Test Director", LocalDate.of(2023, 1, 1), 120, "Action", null, "Test movie description", null);
        entityManager.persist(movie);
        entityManager.flush();
        
        // When
        movieRepository.deleteById(movie.getId());
        
        // Then
        Optional<Movie> found = movieRepository.findById(movie.getId());
        assertFalse(found.isPresent());
    }
}
