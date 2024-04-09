package com.naren.movieticketbookingapplication.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class Customer_MovieNotFound extends RuntimeException {
    public Customer_MovieNotFound(String message) {
        super(message);
    }
}
