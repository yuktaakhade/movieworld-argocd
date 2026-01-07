package com.abc.movieworld.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a movie.
 * Contains basic information about a movie.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Director is required")
    private String director;
    
    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;
    
    private String genre;
    
    @Column(name = "image_path")
    private String imagePath;
    
    @Column(length = 2000)
    private String description;
    
    // This field is not persisted but used to store reviews fetched from the review service
    @jakarta.persistence.Transient
    private Object reviews;
}
