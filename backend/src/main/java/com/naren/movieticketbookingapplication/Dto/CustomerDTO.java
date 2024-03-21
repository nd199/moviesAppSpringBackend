package com.naren.movieticketbookingapplication.Dto;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Long phoneNumber
) {
}
