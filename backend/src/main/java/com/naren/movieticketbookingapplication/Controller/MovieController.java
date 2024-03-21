package com.naren.movieticketbookingapplication.Controller;


import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Record.MovieUpdation;
import com.naren.movieticketbookingapplication.Service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/movie")
    public ResponseEntity<Customer> createCustomer(MovieRegistration registration) {
        movieService.addMovie(registration);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<Movie> getCustomerById(@PathVariable("id") Integer movieId) {
        return new ResponseEntity<>(
                movieService.getMovieById(movieId), HttpStatus.OK);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> customerList() {
        return new ResponseEntity<>(movieService.getMovieList(), HttpStatus.OK);
    }

    @PutMapping("/movie/{id}")
    public ResponseEntity<Movie> updateCustomer(MovieUpdation update, @PathVariable("id") Integer movieId) {
        movieService.updateMovie(update, movieId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
