package com.abc.movieworld.service;

import com.abc.movieworld.dto.MovieDTO;
import com.abc.movieworld.mapper.MovieMapper;
import com.abc.movieworld.model.Movie;
import com.abc.movieworld.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie testMovie;
    private MovieDTO testMovieDTO;
    private List<Movie> testMovies;
    private List<MovieDTO> testMovieDTOs;

    // Mocks for WebClient chain
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testMovie = new Movie(1L, "Test Movie", "Test Director", LocalDate.now(), 120, "Action", "test_movie.jpg", "Test movie description", null);
        Movie movie2 = new Movie(2L, "Test Movie 2", "Test Director 2", LocalDate.now(), 130, "Drama", "test_movie2.jpg", "Test movie 2 description", null);
        testMovies = Arrays.asList(testMovie, movie2);

        testMovieDTO = new MovieDTO(1L, "Test Movie", "Test Director", LocalDate.now(), 120, "Action", "test_movie.jpg", "Test movie description", null);
        MovieDTO movieDTO2 = new MovieDTO(2L, "Test Movie 2", "Test Director 2", LocalDate.now(), 130, "Drama", "test_movie2.jpg", "Test movie 2 description", null);
        testMovieDTOs = Arrays.asList(testMovieDTO, movieDTO2);

        // Set private field for movieReviewServiceUrl
        ReflectionTestUtils.setField(movieService, "movieReviewServiceUrl", "http://fake-url");

        // Mock WebClient builder chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Long.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getAllMovies_ShouldReturnAllMovieDTOs() {
        when(movieRepository.findAll()).thenReturn(testMovies);
        when(movieMapper.toDTOList(testMovies)).thenReturn(testMovieDTOs);

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
        verify(movieRepository).findAll();
        verify(movieMapper).toDTOList(testMovies);
    }

    @Test
    void getMovieById_WithExistingId_ShouldReturnMovieDTO() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieMapper.toDTO(testMovie)).thenReturn(testMovieDTO);

        Optional<MovieDTO> result = movieService.getMovieById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Movie", result.get().getTitle());
        verify(movieRepository).findById(1L);
        verify(movieMapper).toDTO(testMovie);
    }

    @Test
    void getMovieWithReviews_ShouldReturnMovieWithReviews() {
        String reviewsJson = "[{\"id\":1,\"comment\":\"Great!\"}]";
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(reviewsJson));
        when(movieMapper.toDTO(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            return new MovieDTO(movie.getId(), movie.getTitle(), movie.getDirector(), movie.getReleaseDate(), 
                movie.getDurationMinutes(), movie.getGenre(), movie.getImagePath(), movie.getDescription(), movie.getReviews());
        });

        Optional<MovieDTO> result = movieService.getMovieWithReviews(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getReviews());
        assertEquals(reviewsJson, result.get().getReviews());
        verify(movieRepository).findById(1L);
    }

    @Test
    void createMovie_ShouldSaveAndReturnMovieDTO() {
        when(movieMapper.toEntity(testMovieDTO)).thenReturn(testMovie);
        when(movieRepository.save(testMovie)).thenReturn(testMovie);
        when(movieMapper.toDTO(testMovie)).thenReturn(testMovieDTO);

        MovieDTO result = movieService.createMovie(testMovieDTO);

        assertNotNull(result);
        assertEquals("Test Movie", result.getTitle());
        verify(movieRepository).save(testMovie);
        verify(movieMapper).toEntity(testMovieDTO);
        verify(movieMapper).toDTO(testMovie);
    }

    @Test
    void updateMovie_WithExistingId_ShouldUpdateAndReturnMovieDTO() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);
        when(movieMapper.toDTO(testMovie)).thenReturn(testMovieDTO);

        MovieDTO result = movieService.updateMovie(1L, testMovieDTO);

        assertNotNull(result);
        verify(movieRepository).findById(1L);
        verify(movieRepository).save(testMovie);
        verify(movieMapper).updateEntityFromDTO(testMovie, testMovieDTO);
        verify(movieMapper).toDTO(testMovie);
    }

    @Test
    void deleteMovie_WithExistingId_ShouldReturnTrue() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1L);

        boolean result = movieService.deleteMovie(1L);

        assertTrue(result);
        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }
}
