package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Repo.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class MovieDaoImpl implements MovieDao {

    private final MovieRepository movieRepository;

    public MovieDaoImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void addMovie(Movie movie) {
        movieRepository.save(movie);
    }

    @Override
    public void removeMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public void updateMovie(Movie update) {
        movieRepository.save(update);
    }

    @Override
    public boolean existsByName(String name) {
        return movieRepository.existsByName(name);
    }

    @Override
    public List<Movie> getMovieList() {
        Page<Movie> movies = movieRepository.findAll(Pageable.ofSize(1000));
        return movies.getContent();
    }
}
