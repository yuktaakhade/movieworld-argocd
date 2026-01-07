package com.abc.movieworld.controller;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MovieController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDTO testMovieDTO;
    private List<MovieDTO> testMovieDTOs;

    @BeforeEach
    void setUp() {
        testMovieDTO = new MovieDTO(1L, "Test Movie", "Test Director", LocalDate.now(), 120, "Action", "test-movie.jpg", "Test movie description", null);
        MovieDTO movieDTO2 = new MovieDTO(2L, "Test Movie 2", "Test Director 2", LocalDate.now(), 130, "Drama", "test-movie-2.jpg", "Test movie 2 description", null);
        testMovieDTOs = Arrays.asList(testMovieDTO, movieDTO2);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(testMovieDTOs);

        mockMvc.perform(get("/api/movieworld"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Movie")));

        verify(movieService).getAllMovies();
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovie() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(testMovieDTO));

        mockMvc.perform(get("/api/movieworld/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Test Movie")));

        verify(movieService).getMovieById(1L);
    }

    @Test
    void getMovieWithReviews_ShouldReturnMovieWithReviews() throws Exception {
        testMovieDTO.setReviews("Some reviews");
        when(movieService.getMovieWithReviews(1L)).thenReturn(Optional.of(testMovieDTO));

        mockMvc.perform(get("/api/movieworld/1/with-reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reviews", is("Some reviews")));

        verify(movieService).getMovieWithReviews(1L);
    }

    @Test
    void createMovie_ShouldReturnCreatedMovie() throws Exception {
        when(movieService.createMovie(any(MovieDTO.class))).thenReturn(testMovieDTO);

        mockMvc.perform(post("/api/movieworld")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMovieDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Test Movie")));

        verify(movieService).createMovie(any(MovieDTO.class));
    }

    @Test
    void updateMovie_WithExistingId_ShouldReturnUpdatedMovie() throws Exception {
        when(movieService.updateMovie(eq(1L), any(MovieDTO.class))).thenReturn(testMovieDTO);

        mockMvc.perform(put("/api/movieworld/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMovieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Movie")));

        verify(movieService).updateMovie(eq(1L), any(MovieDTO.class));
    }

    @Test
    void deleteMovie_WithExistingId_ShouldReturnNoContent() throws Exception {
        when(movieService.deleteMovie(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/movieworld/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }
}
