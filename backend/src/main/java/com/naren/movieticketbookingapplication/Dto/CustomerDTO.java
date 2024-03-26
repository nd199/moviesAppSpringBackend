package com.naren.movieticketbookingapplication.Dto;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        Long phoneNumber
) {
}
