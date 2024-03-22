package com.naren.movieticketbookingapplication.Controller;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;
import com.naren.movieticketbookingapplication.Service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/movies")
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRegistration registration) {
        log.info("Received request to create movie with registration: {}", registration);
        movieService.addMovie(registration);
        log.info("Movie created successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable("id") Integer movieId) {
        log.info("Received request to retrieve movie with ID: {}", movieId);
        Movie movie = movieService.getMovieById(movieId);
        log.info("Retrieved movie: {}", movie);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> movieList() {
        log.info("Received request to retrieve list of movies");
        List<Movie> movies = movieService.getMovieList();
        log.info("Retrieved list of movies: {}", movies);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<Movie> updateMovie(MovieUpdation update, @PathVariable("id") Integer movieId) {
        log.info("Received request to update movie with ID: {}", movieId);
        movieService.updateMovie(update, movieId);
        log.info("Movie updated successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Integer movieId) {
        log.info("Received request to delete movie with ID: {}", movieId);
        movieService.removeMovie(movieId);
        log.info("Movie deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
