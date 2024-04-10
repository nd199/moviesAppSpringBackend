package com.naren.movieticketbookingapplication.Dto;

import com.naren.movieticketbookingapplication.Entity.Movie;

import java.util.List;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        List<String> roles,
        Long phoneNumber,
        String userName,
        List<Movie> movies) {
}
