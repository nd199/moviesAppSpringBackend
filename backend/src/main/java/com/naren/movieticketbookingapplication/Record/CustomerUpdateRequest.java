package com.naren.movieticketbookingapplication.Record;

public record CustomerUpdateRequest(
        String name,
        String email,
        Long phone
) {
}
