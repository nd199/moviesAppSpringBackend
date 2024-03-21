package com.naren.movieticketbookingapplication;


import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AbstractTestContainers {

    protected static final Faker FAKER = new Faker();
    @Container
    protected static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("postgres-movie")
                    .withUsername("codeNaren")
                    .withPassword("password");

    @BeforeAll
    static void engageFlyway() {
        Flyway flyway = Flyway
                .configure()
                .dataSource(postgresContainer.getJdbcUrl(),
                        postgresContainer.getUsername(),
                        postgresContainer.getPassword()
                )
                .load();
        flyway.migrate();
    }

    @DynamicPropertySource
    private static void registerDynamicPropertySource(
            DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgresContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgresContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgresContainer::getPassword
        );
    }

}

