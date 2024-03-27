package com.naren.movieticketbookingapplication.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.NoSuchAlgorithmException;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AlgorithmNotSupportedException extends RuntimeException {
    public AlgorithmNotSupportedException(String message) {
        super(message);
    }

    public AlgorithmNotSupportedException(String message, NoSuchAlgorithmException e) {
        super(message, e);
    }
}
