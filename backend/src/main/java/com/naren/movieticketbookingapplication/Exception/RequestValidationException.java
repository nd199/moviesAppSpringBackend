package com.naren.movieticketbookingapplication.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class RequestValidationException extends RuntimeException {

    public RequestValidationException(String message) {
        super(message);
        log.error("RequestValidationException: {}", message);
    }
}