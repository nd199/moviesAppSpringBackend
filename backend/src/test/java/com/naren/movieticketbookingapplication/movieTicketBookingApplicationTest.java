package com.naren.movieticketbookingapplication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class movieTicketBookingApplicationTest {

    @Test
    void NotThrowsWhilePassingArgsInMain() {
        String[] args = {};

        Assertions.assertDoesNotThrow(
                () -> movieTicketBookingApplication.main(args)
        );
    }
}