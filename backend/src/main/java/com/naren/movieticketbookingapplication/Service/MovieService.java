package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;

import java.util.List;


public interface MovieService {
    void addMovie(MovieRegistration Registration);

    void removeMovie(Integer id);

    Movie getMovieById(Integer id);

    List<Movie> getMovieList();

    void updateMovie(MovieUpdation update, Integer movieId);
}
