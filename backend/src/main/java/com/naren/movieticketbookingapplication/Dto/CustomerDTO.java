package com.naren.movieticketbookingapplication.Dto;

import java.util.List;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        List<String> roles,
        Long phoneNumber,
        String userName
) {
}
