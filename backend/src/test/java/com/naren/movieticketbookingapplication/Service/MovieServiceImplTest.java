package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.MovieDao;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Exception.RequestValidationException;
import com.naren.movieticketbookingapplication.Exception.ResourceAlreadyExists;
import com.naren.movieticketbookingapplication.Exception.ResourceNotFoundException;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieDao movieDao;
    private MovieService underTest;

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImplTest.class);

    @BeforeEach
    void setUp() {
        underTest = new MovieServiceImpl(movieDao);
    }

    @Test
    void addMovie() {
        MovieRegistration registration = new MovieRegistration("testName", 300.23, 5.00);

        when(movieDao.existsByName("testName")).thenReturn(false);

        underTest.addMovie(registration);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).addMovie(movieArgumentCaptor.capture());

        Movie captured = movieArgumentCaptor.getValue();

        assertThat(captured.getMovie_id()).isNull();
        assertThat(captured.getName()).isEqualTo(registration.name());
        assertThat(captured.getCost()).isEqualTo(registration.cost());
        assertThat(captured.getRating()).isEqualTo(registration.rating());
    }

    @Test
    void throwsMovieNameExists() {
        MovieRegistration registration = new MovieRegistration("testName", 300.23, 5.00);

        when(movieDao.existsByName("testName")).thenReturn(true);

        assertThatThrownBy(
                () -> underTest.addMovie(registration))
                .isInstanceOf(ResourceAlreadyExists.class)
                .hasMessageContaining("Movie name %s already exists".formatted(registration.name())
                );

        verify(movieDao, never()).addMovie(any());
    }

    @Test
    void removeMovie() {
        long id = 1;
        Movie movie = new Movie("testName", 300.22, 5.00);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        underTest.removeMovie(id);

        verify(movieDao).removeMovie(movie);
    }

    @Test
    void throwsWhenMovieRemovalIfNotExist() {
        long id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.removeMovie(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie with ID %s not found".formatted(id));

        verify(movieDao, never()).removeMovie(any());
    }

    @Test
    void getMovieById() {
        long id = 1;
        Movie movie = new Movie("testName", 300.22, 5.00);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        Movie actual = underTest.getMovieById(id);

        assertThat(actual).isEqualTo(movie);
    }

    @Test
    void getMovieByIdThrowsIfNotExists() {
        long id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getMovieById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie with ID '1' not found");
    }

    @Test
    void getMovieList() {
        underTest.getMovieList();

        verify(movieDao).getMovieList();
    }

    @Test
    void updateMovie() {
        long id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName2", 300.00, 5.0);

        underTest.updateMovie(movieUpdation, id);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).updateMovie(movieArgumentCaptor.capture());

        Movie updatedMovie = movieArgumentCaptor.getValue();

        assertThat(updatedMovie.getName()).isEqualTo(movieUpdation.name());
        assertThat(updatedMovie.getCost()).isEqualTo(movieUpdation.cost());
        assertThat(updatedMovie.getRating()).isEqualTo(movieUpdation.rating());
    }

    @Test
    void throwsIfNoChangesFoundForUpdation() {
        long id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName", 200.0, 2.0);

        assertThatThrownBy(() -> underTest.updateMovie(movieUpdation, id))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining("No data changes found");
    }

    @Test
    void updateMovieByIdThrowsIfNotExists() {
        long id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        MovieUpdation updation = new MovieUpdation("Name", 220.0, 3.30);

        assertThatThrownBy(() -> underTest.updateMovie(updation, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movie not found");

        verify(movieDao, never()).updateMovie(any());
    }
}
