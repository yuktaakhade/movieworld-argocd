package com.abc.movieworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Data Transfer Object for Movie entity.
 * Used for transferring movie data between layers.
 * Can be used for create, update, and read operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movie Data Transfer Object")
public class MovieDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "Movie ID (null for creation)")
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Schema(description = "Movie title", required = true)
    private String title;
    
    @NotBlank(message = "Director is required")
    @Schema(description = "Movie director", required = true)
    private String director;
    
    @NotNull(message = "Release date is required")
    @Schema(description = "Movie release date", required = true)
    private LocalDate releaseDate;
    
    @Positive(message = "Duration must be positive")
    @Schema(description = "Movie duration in minutes")
    private Integer durationMinutes;
    
    @Schema(description = "Movie genre")
    private String genre;
    
    @Schema(description = "Movie image path (relative path to static image)")
    private String imagePath;
    
    @Schema(description = "Movie description")
    private String description;
    
    @Schema(description = "Movie reviews (only populated when fetching with reviews)")
    private transient Object reviews;
}
