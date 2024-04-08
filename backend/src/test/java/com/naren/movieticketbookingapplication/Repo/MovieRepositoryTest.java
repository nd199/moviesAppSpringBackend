package com.naren.movieticketbookingapplication.Repo;

import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class MovieRepositoryTest {

    @Autowired
    private MovieRepository underTest;


    @Test
    void existsByName() {
        Movie movie = new Movie(
                "harryPotter",
                200D, 5D
        );

        underTest.save(movie);

        assertThat(underTest.existsByName(movie.getName())).isTrue();
    }
}