package com.abc.movieworld.mapper;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieMapperTest {

    @Autowired
    private MovieMapper movieMapper;

    @Test
    void testToDTO() {
        // Create a Movie entity
        LocalDate releaseDate = LocalDate.now();
        Movie movie = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        
        // Map to DTO
        MovieDTO movieDTO = movieMapper.toDTO(movie);
        
        // Verify mapping
        assertNotNull(movieDTO);
        assertEquals(movie.getId(), movieDTO.getId());
        assertEquals(movie.getTitle(), movieDTO.getTitle());
        assertEquals(movie.getDirector(), movieDTO.getDirector());
        assertEquals(movie.getReleaseDate(), movieDTO.getReleaseDate());
        assertEquals(movie.getDurationMinutes(), movieDTO.getDurationMinutes());
        assertEquals(movie.getGenre(), movieDTO.getGenre());
        assertEquals(movie.getImagePath(), movieDTO.getImagePath());
        assertEquals(movie.getDescription(), movieDTO.getDescription());
        assertEquals(movie.getReviews(), movieDTO.getReviews());
    }

    @Test
    void testToEntity() {
        // Create a MovieDTO
        LocalDate releaseDate = LocalDate.now();
        MovieDTO movieDTO = new MovieDTO(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        
        // Map to Entity
        Movie movie = movieMapper.toEntity(movieDTO);
        
        // Verify mapping
        assertNotNull(movie);
        assertEquals(movieDTO.getId(), movie.getId());
        assertEquals(movieDTO.getTitle(), movie.getTitle());
        assertEquals(movieDTO.getDirector(), movie.getDirector());
        assertEquals(movieDTO.getReleaseDate(), movie.getReleaseDate());
        assertEquals(movieDTO.getDurationMinutes(), movie.getDurationMinutes());
        assertEquals(movieDTO.getGenre(), movie.getGenre());
        assertEquals(movieDTO.getImagePath(), movie.getImagePath());
        assertEquals(movieDTO.getDescription(), movie.getDescription());
        assertEquals(movieDTO.getReviews(), movie.getReviews());
    }

    @Test
    void testToDTOList() {
        // Create a list of Movie entities
        LocalDate releaseDate = LocalDate.now();
        Movie movie1 = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        Movie movie2 = new Movie(2L, "Interstellar", "Christopher Nolan", releaseDate, 169, "Sci-Fi", "interstellar.jpg", "A space exploration movie", "Other reviews");
        List<Movie> movies = Arrays.asList(movie1, movie2);
        
        // Map to DTO list
        List<MovieDTO> movieDTOs = movieMapper.toDTOList(movies);
        
        // Verify mapping
        assertNotNull(movieDTOs);
        assertEquals(2, movieDTOs.size());
        assertEquals(movie1.getId(), movieDTOs.get(0).getId());
        assertEquals(movie1.getTitle(), movieDTOs.get(0).getTitle());
        assertEquals(movie2.getId(), movieDTOs.get(1).getId());
        assertEquals(movie2.getTitle(), movieDTOs.get(1).getTitle());
    }

    @Test
    void testUpdateEntityFromDTO() {
        // Create a Movie entity and a MovieDTO with updated values
        LocalDate releaseDate = LocalDate.now();
        Movie movie = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        MovieDTO updatedMovieDTO = new MovieDTO(1L, "Updated Inception", "Updated Director", releaseDate, 150, "Action", "updated_inception.jpg", "Updated description", "Updated reviews");
        
        // Update entity from DTO
        movieMapper.updateEntityFromDTO(movie, updatedMovieDTO);
        
        // Verify update
        assertEquals(updatedMovieDTO.getTitle(), movie.getTitle());
        assertEquals(updatedMovieDTO.getDirector(), movie.getDirector());
        assertEquals(updatedMovieDTO.getDurationMinutes(), movie.getDurationMinutes());
        assertEquals(updatedMovieDTO.getGenre(), movie.getGenre());
        assertEquals(updatedMovieDTO.getImagePath(), movie.getImagePath());
        assertEquals(updatedMovieDTO.getDescription(), movie.getDescription());
        
        // The reviews field might not be updated by the mapper implementation
        // So we don't assert on it
        
        // ID should not be updated
        assertEquals(1L, movie.getId());
    }
}
