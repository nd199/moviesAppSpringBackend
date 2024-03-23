package com.naren.movieticketbookingapplication.Service;


import com.naren.movieticketbookingapplication.Dao.MovieDao;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Exception.RequestValidationException;
import com.naren.movieticketbookingapplication.Exception.ResourceAlreadyExists;
import com.naren.movieticketbookingapplication.Exception.ResourceNotFoundException;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


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
            throw new ResourceAlreadyExists("Movie name Taken");
        }
        movieDao.addMovie(movie);
    }

    private Movie createMovie(MovieRegistration create) {
        return new Movie(
                create.name(),
                create.cost(),
                create.rating()
        );
    }

    @Override
    public void removeMovie(Integer id) {
        Movie movie = movieDao.getMovieById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Movie with [%s] not found".formatted(id)
                        )
                );
        movieDao.removeMovie(movie);
    }

    @Override
    public Movie getMovieById(Integer id) {
        return movieDao.getMovieById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Movie with [%s] not found".formatted(id)
                        )
                );
    }

    @Override
    public List<Movie> getMovieList() {
        return movieDao.getMovieList();
    }

    @Override
    public void updateMovie(MovieUpdation update, Integer movieId) {
        Movie movie = movieDao.getMovieById(movieId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Movie not found"
                )
        );

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
            throw new RequestValidationException("no data changes found");
        }
        movieDao.updateMovie(movie);
    }
}
