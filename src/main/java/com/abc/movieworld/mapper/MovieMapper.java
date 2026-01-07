package com.abc.movieworld.mapper;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.model.Movie;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper class for converting between Movie entity and MovieDTO.
 */
@Component
public class MovieMapper {

    /**
     * Convert Movie entity to MovieDTO.
     * @param movie Movie entity
     * @return MovieDTO
     */
    public MovieDTO toDTO(Movie movie) {
        if (movie == null) {
            return null;
        }
        
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDirector(movie.getDirector());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setGenre(movie.getGenre());
        dto.setImagePath(movie.getImagePath());
        dto.setDescription(movie.getDescription());
        dto.setReviews(movie.getReviews());
        
        return dto;
    }
    
    /**
     * Convert MovieDTO to Movie entity.
     * @param dto MovieDTO
     * @return Movie entity
     */
    public Movie toEntity(MovieDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setGenre(dto.getGenre());
        movie.setImagePath(dto.getImagePath());
        movie.setDescription(dto.getDescription());
        movie.setReviews(dto.getReviews());
        
        return movie;
    }
    
    /**
     * Update Movie entity from MovieDTO.
     * @param movie Movie entity to update
     * @param dto MovieDTO with updated values
     * @return Updated Movie entity
     */
    public Movie updateEntityFromDTO(Movie movie, MovieDTO dto) {
        if (movie == null || dto == null) {
            return movie;
        }
        
        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setGenre(dto.getGenre());
        movie.setImagePath(dto.getImagePath());
        movie.setDescription(dto.getDescription());
        
        return movie;
    }
    
    /**
     * Convert list of Movie entities to list of MovieDTOs.
     * @param movies List of Movie entities
     * @return List of MovieDTOs
     */
    public List<MovieDTO> toDTOList(List<Movie> movies) {
        if (movies == null) {
            return Collections.emptyList();
        }
        
        return movies.stream()
                .map(this::toDTO)
                .toList();
    }
}
