package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;

import java.util.List;


public interface MovieService {
    void addMovie(MovieRegistration Registration);

    void removeMovie(Long id);

    Movie getMovieById(Long id);

    List<Movie> getMovieList();

    void updateMovie(MovieUpdation update, Long movieId);
}
