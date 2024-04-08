package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.MovieDao;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Exception.RequestValidationException;
import com.naren.movieticketbookingapplication.Exception.ResourceAlreadyExists;
import com.naren.movieticketbookingapplication.Exception.ResourceNotFoundException;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Transactional
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieDao movieDao;

    public MovieServiceImpl(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    @Override
    public void addMovie(MovieRegistration registration) {
        Movie movie = createMovie(registration);
        if (movieDao.existsByName(registration.name())) {
            String errorMessage = "Movie name '%s' already exists".formatted(registration.name());
            log.error(errorMessage);
            throw new ResourceAlreadyExists(errorMessage);
        }
        movieDao.addMovie(movie);
        log.info("Movie added successfully: {}", movie);
    }

    private Movie createMovie(MovieRegistration registration) {
        return new Movie(
                registration.name(),
                registration.cost(),
                registration.rating()
        );
    }

    @Override
    public void removeMovie(Long id) {
        Movie movie = movieDao.getMovieById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Movie with ID '%s' not found".formatted(id);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
        movieDao.removeMovie(movie);
        log.info("Movie removed successfully: {}", movie);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieDao.getMovieById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Movie with ID '%s' not found".formatted(id);
                    log.error(errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    @Override
    public List<Movie> getMovieList() {
        List<Movie> movies = movieDao.getMovieList();
        log.info("Retrieved {} movies", movies.size());
        return movies;
    }

    @Override
    public void updateMovie(MovieUpdation update, Long movieId) {
        Movie movie = movieDao.getMovieById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        boolean changes = false;

        if (update.name() != null && !update.name().equals(movie.getName())) {
            changes = true;
            movie.setName(update.name());
        }
        if (update.cost() != null && !update.cost().equals(movie.getCost())) {
            changes = true;
            movie.setCost(update.cost());
        }
        if (update.rating() != null && !update.rating().equals(movie.getRating())) {
            changes = true;
            movie.setRating(update.rating());
        }
        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }
        movieDao.updateMovie(movie);
        log.info("Movie updated successfully: {}", movie);
    }
}
