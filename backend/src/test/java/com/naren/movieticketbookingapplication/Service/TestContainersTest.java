package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.AbstractTestContainers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestContainersTest extends AbstractTestContainers {

    @Test
    void canStartPostgresDb() {
        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
    }
}
