package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Repo.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MovieDaoImpl implements MovieDao {

    private final MovieRepository movieRepository;

    public MovieDaoImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void addMovie(Movie movie) {
        log.info("Adding movie: {}", movie);
        movieRepository.save(movie);
        log.info("Movie added successfully: {}", movie);
    }

    @Override
    public void removeMovie(Movie movie) {
        log.info("Removing movie: {}", movie);
        movieRepository.delete(movie);
        log.info("Movie removed successfully: {}", movie);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        log.info("Fetching movie by ID: {}", id);
        Optional<Movie> movie = movieRepository.findById(id);
        log.info("Movie fetched: {}", movie.orElse(null));
        return movie;
    }

    @Override
    public void updateMovie(Movie movie) {
        log.info("Updating movie: {}", movie);
        movieRepository.save(movie);
        log.info("Movie updated successfully: {}", movie);
    }

    @Override
    public boolean existsByName(String name) {
        log.info("Checking if movie exists by name: {}", name);
        boolean exists = movieRepository.existsByName(name);
        log.info("Movie exists by name '{}': {}", name, exists);
        return exists;
    }

    @Override
    public List<Movie> getMovieList() {
        log.info("Fetching list of movies");
        Page<Movie> movies = movieRepository.findAll(Pageable.ofSize(1000));
        List<Movie> movieList = movies.getContent();
        log.info("Fetched {} movies", movieList.size());
        return movieList;
    }
}
