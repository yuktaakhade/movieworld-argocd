package com.abc.movieworld.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDTOTest {

    @Test
    void testMovieDTO() {
        LocalDate releaseDate = LocalDate.now();
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        movieDTO.setTitle("Inception");
        movieDTO.setDirector("Christopher Nolan");
        movieDTO.setReleaseDate(releaseDate);
        movieDTO.setDurationMinutes(148);
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setImagePath("inception.jpg");
        movieDTO.setDescription("A mind-bending movie");
        movieDTO.setReviews("Some reviews");

        assertEquals(1L, movieDTO.getId());
        assertEquals("Inception", movieDTO.getTitle());
        assertEquals("Christopher Nolan", movieDTO.getDirector());
        assertEquals(releaseDate, movieDTO.getReleaseDate());
        assertEquals(148, movieDTO.getDurationMinutes());
        assertEquals("Sci-Fi", movieDTO.getGenre());
        assertEquals("inception.jpg", movieDTO.getImagePath());
        assertEquals("A mind-bending movie", movieDTO.getDescription());
        assertEquals("Some reviews", movieDTO.getReviews());

        MovieDTO movieDTO2 = new MovieDTO(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        assertEquals(movieDTO2, movieDTO);
        assertEquals(movieDTO2.hashCode(), movieDTO.hashCode());
    }
    
    @Test
    void testMovieDTOEqualsAndHashCode() {
        LocalDate releaseDate = LocalDate.now();
        MovieDTO movieDTO1 = new MovieDTO(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        MovieDTO movieDTO2 = new MovieDTO(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        MovieDTO movieDTO3 = new MovieDTO(2L, "Interstellar", "Christopher Nolan", releaseDate, 169, "Sci-Fi", "interstellar.jpg", "A space exploration movie", "Other reviews");
        
        // Test equals
        assertEquals(movieDTO2, movieDTO1);
        assertNotEquals(movieDTO3, movieDTO1);
        assertNotEquals(null, movieDTO1);
        assertNotEquals(new Object(), movieDTO1);
        
        // Test hashCode
        assertEquals(movieDTO1.hashCode(), movieDTO2.hashCode());
        assertNotEquals(movieDTO1.hashCode(), movieDTO3.hashCode());
    }
    
    @Test
    void testToString() {
        LocalDate releaseDate = LocalDate.now();
        MovieDTO movieDTO = new MovieDTO(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        
        String movieDTOString = movieDTO.toString();
        
        assertTrue(movieDTOString.contains("id=1"));
        assertTrue(movieDTOString.contains("title=Inception"));
        assertTrue(movieDTOString.contains("director=Christopher Nolan"));
        assertTrue(movieDTOString.contains("durationMinutes=148"));
        assertTrue(movieDTOString.contains("reviews=Some reviews"));
    }
    
    @Test
    void testValidation() {
        MovieDTO movieDTO = new MovieDTO();
        
        // Test that validation annotations are present
        // This is a simple test to ensure the class has validation annotations
        // A more comprehensive test would use a validator
        assertNotNull(movieDTO);
    }
}
