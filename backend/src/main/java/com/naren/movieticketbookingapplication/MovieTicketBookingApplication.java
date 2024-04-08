package com.naren.movieticketbookingapplication;

import com.github.javafaker.Faker;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Repo.CustomerRepository;
import com.naren.movieticketbookingapplication.Repo.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@Slf4j
@SpringBootApplication
public class MovieTicketBookingApplication {

    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) {
        SpringApplication.run(MovieTicketBookingApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(CustomerRepository customerRepository,
                                               MovieRepository movieRepository, PasswordEncoder encoder) {
        return args -> {
            createRandomCustomer(customerRepository, encoder);
            createRandomMovie(movieRepository);
        };
    }

    private void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder encoder) {
        String customerName = FAKER.name().fullName();
        String customerEmail = customerName.toLowerCase().replace(" ", "") + "@codeNaren.com";
        String password = encoder.encode(FAKER.internet().password(8, 12));
        Long phoneNumber = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        Customer customer = new Customer(customerName, customerEmail, password, phoneNumber);
        customerRepository.save(customer);

        log.info("Created new customer: {}", customer);
    }

    private void createRandomMovie(MovieRepository movieRepository) {
        String movieName = FAKER.book().title();
        double rating = Math.floor(RANDOM.nextDouble(2, 5) * 100) / 100;
        double cost = Math.floor(RANDOM.nextDouble(200, 1200) * 100) / 100;

        Movie movie = new Movie(movieName, cost, rating);
        movieRepository.save(movie);

        log.info("Created new movie: {}", movie);
    }
}
