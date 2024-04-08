package com.naren.movieticketbookingapplication.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@Slf4j
public class AlgorithmNotSupportedException extends RuntimeException {


    public AlgorithmNotSupportedException(String message) {
        super(message);
        log.error("AlgorithmNotSupportedException: {}", message);
    }
}