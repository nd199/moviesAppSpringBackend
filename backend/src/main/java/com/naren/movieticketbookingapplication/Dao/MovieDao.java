package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieDao {
    void addMovie(Movie movie);

    void removeMovie(Movie movie);

    Optional<Movie> getMovieById(Long id);

    void updateMovie(Movie update);

    boolean existsByName(String name);

    List<Movie> getMovieList();
}

