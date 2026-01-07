package com.abc.movieworld.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MovieTest {

    @Test
    void testMovieModel() {
        LocalDate releaseDate = LocalDate.now();
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDirector("Christopher Nolan");
        movie.setReleaseDate(releaseDate);
        movie.setDurationMinutes(148);
        movie.setGenre("Sci-Fi");
        movie.setImagePath("inception.jpg");
        movie.setDescription("A mind-bending movie");
        movie.setReviews("Some reviews");

        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals("Christopher Nolan", movie.getDirector());
        assertEquals(releaseDate, movie.getReleaseDate());
        assertEquals(148, movie.getDurationMinutes());
        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals("inception.jpg", movie.getImagePath());
        assertEquals("A mind-bending movie", movie.getDescription());
        assertEquals("Some reviews", movie.getReviews());

        Movie movie2 = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        assertEquals(movie, movie2);
        assertEquals(movie.hashCode(), movie2.hashCode());
    }
    
    @Test
    void testMovieEqualsAndHashCode() {
        LocalDate releaseDate = LocalDate.now();
        Movie movie1 = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        Movie movie2 = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        Movie movie3 = new Movie(2L, "Interstellar", "Christopher Nolan", releaseDate, 169, "Sci-Fi", "interstellar.jpg", "A space exploration movie", "Other reviews");
        
        // Test equals
        assertEquals(movie2, movie1);
        assertNotEquals(movie3, movie1);
        assertNotEquals(null, movie1);
        assertNotEquals(new Object(), movie1);
        
        // Test hashCode
        assertEquals(movie1.hashCode(), movie2.hashCode());
        assertNotEquals(movie1.hashCode(), movie3.hashCode());
    }
    
    @Test
    void testToString() {
        LocalDate releaseDate = LocalDate.now();
        Movie movie = new Movie(1L, "Inception", "Christopher Nolan", releaseDate, 148, "Sci-Fi", "inception.jpg", "A mind-bending movie", "Some reviews");
        
        String movieString = movie.toString();
        
        assertTrue(movieString.contains("id=1"));
        assertTrue(movieString.contains("title=Inception"));
        assertTrue(movieString.contains("director=Christopher Nolan"));
        assertTrue(movieString.contains("durationMinutes=148"));
        assertTrue(movieString.contains("reviews=Some reviews"));
    }
}
