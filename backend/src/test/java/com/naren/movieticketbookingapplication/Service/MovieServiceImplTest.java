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
    void ThrowsMovieNameExists() {

        MovieRegistration registration = new MovieRegistration("testName", 300.23, 5.00);

        when(movieDao.existsByName("testName")).thenReturn(true);

        assertThatThrownBy(() -> underTest.addMovie(registration)).isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Movie name Taken");

        verify(movieDao, never()).addMovie(any());

    }

    @Test
    void removeMovie() {
        int id = 1;
        Movie movie = new Movie("testName", 300.22, 5.00);
        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));
        underTest.removeMovie(id);
        verify(movieDao).removeMovie(movie);
    }

    @Test
    void throwsWhenMovieRemovalIfNotExist() {
        int id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.removeMovie(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie with [%s] not found".formatted(id));

        verify(movieDao, never()).removeMovie(any());
    }

    @Test
    void getMovieById() {
        int id = 1;
        Movie movie = new Movie("testName", 300.22, 5.00);
        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));
        Movie actual = underTest.getMovieById(id);
        assertThat(actual).isEqualTo(movie);
    }


    @Test
    void getMovieByIdThrowsIfNotExists() {
        int id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getMovieById(id)).isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage("Movie with [%s] not found".formatted(id));
    }

    @Test
    void getMovieList() {
        underTest.getMovieList();
        verify(movieDao).getMovieList();
    }

    @Test
    void updateMovie() {
        int id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName2", 300.00, 5.0);

        underTest.updateMovie(movieUpdation, id);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).updateMovie(movieArgumentCaptor.capture());

        Movie expected = movieArgumentCaptor.getValue();

        assertThat(expected.getName()).isEqualTo(movieUpdation.name());
        assertThat(expected.getCost()).isEqualTo(movieUpdation.cost());
        assertThat(expected.getRating()).isEqualTo(movieUpdation.rating());
    }


    @Test
    void CanUpdateOnlyMovieName() {
        int id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName2", 200.0, 2.0);

        underTest.updateMovie(movieUpdation, id);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).updateMovie(movieArgumentCaptor.capture());

        Movie expected = movieArgumentCaptor.getValue();

        assertThat(expected.getName()).isEqualTo(movieUpdation.name());
        assertThat(expected.getCost()).isEqualTo(movieUpdation.cost());
        assertThat(expected.getRating()).isEqualTo(movieUpdation.rating());
    }

    @Test
    void canUpdateMovieRatingOnly() {
        int id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName", 200.0, 3.0);

        underTest.updateMovie(movieUpdation, id);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).updateMovie(movieArgumentCaptor.capture());

        Movie expected = movieArgumentCaptor.getValue();

        assertThat(expected.getName()).isEqualTo(movieUpdation.name());
        assertThat(expected.getCost()).isEqualTo(movieUpdation.cost());
        assertThat(expected.getRating()).isEqualTo(movieUpdation.rating());
    }


    @Test
    void canUpdateMovieCostOnly() {
        int id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName", 500.0, 2.0);

        underTest.updateMovie(movieUpdation, id);

        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieDao).updateMovie(movieArgumentCaptor.capture());

        Movie expected = movieArgumentCaptor.getValue();

        assertThat(expected.getName()).isEqualTo(movieUpdation.name());
        assertThat(expected.getCost()).isEqualTo(movieUpdation.cost());
        assertThat(expected.getRating()).isEqualTo(movieUpdation.rating());
    }

    @Test
    void ThrowsIfNoChangesFoundForUpdation() {
        int id = 2;

        Movie movie = new Movie(id, "testName", 200.0, 2.0);

        when(movieDao.getMovieById(id)).thenReturn(Optional.of(movie));

        MovieUpdation movieUpdation = new MovieUpdation("testName", 200.0, 2.0);


        assertThatThrownBy(() -> underTest.updateMovie(movieUpdation, id))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");
    }

    @Test
    void updateMovieByIdThrowsIfNotExists() {
        int id = 1;

        when(movieDao.getMovieById(id)).thenReturn(Optional.empty());

        MovieUpdation updation = new MovieUpdation("Name", 220.0, 3.30);
        assertThatThrownBy(() -> underTest.updateMovie(updation, id)).isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage("Movie not found");

        verify(movieDao, never()).updateMovie(any());
    }


}