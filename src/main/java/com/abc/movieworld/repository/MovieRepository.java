package com.abc.movieworld.repository;

import com.abc.movieworld.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Movie entity.
 * Provides CRUD operations for Movie entities.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Spring Data JPA provides basic CRUD operations by default
    // Custom query methods can be added here if needed
}
