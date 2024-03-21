package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Repo.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieDaoImplTest {

    private MovieDaoImpl underTest;

    private AutoCloseable autoCloseable;

    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new MovieDaoImpl(movieRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Movie getNewMovie() {
        return new Movie(1,
                "Harry Potter",
                200D,
                5D);
    }

    @Test
    void addMovie() {
        Movie movie = getNewMovie();

        underTest.addMovie(movie);

        verify(movieRepository).save(movie);
    }


    @Test
    void getMovieById() {
        Movie movie = getNewMovie();

        Integer id = movie.getMovie_id();

        underTest.getMovieById(id);

        verify(movieRepository).findById(id);
    }

    @Test
    void updateMovie() {
        Movie movie = getNewMovie();

        underTest.updateMovie(movie);

        verify(movieRepository).save(movie);
    }

    @Test
    void existsByName() {
        Movie movie = getNewMovie();

        underTest.existsByName(movie.getName());

        verify(movieRepository).existsByName(movie.getName());
    }

    @Test
    void getMovieList() {
        Page<Movie> page = mock(Page.class);
        List<Movie> movies = List.of(new Movie());

        when(page.getContent()).thenReturn(movies);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<Movie> expected = underTest.getMovieList();

        assertThat(expected).isEqualTo(movies);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(movieRepository).findAll(pageableArgumentCaptor.capture());
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(1000));
    }

    @Test
    void removeMovieById() {
        Movie movie = getNewMovie();

        underTest.removeMovie(movie);

        verify(movieRepository).delete(movie);
    }
}