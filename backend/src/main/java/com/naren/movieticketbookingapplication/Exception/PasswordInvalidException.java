package com.naren.movieticketbookingapplication.Exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Slf4j
public class PasswordInvalidException extends RuntimeException {

    public PasswordInvalidException(String message) {
        super(message);
        log.error("PasswordInvalidException: {}", message);
    }
}
