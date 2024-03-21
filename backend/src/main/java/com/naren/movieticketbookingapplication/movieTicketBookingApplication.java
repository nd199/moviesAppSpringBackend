package com.naren.movieticketbookingapplication;

import com.github.javafaker.Faker;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Repo.CustomerRepository;
import com.naren.movieticketbookingapplication.Repo.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class movieTicketBookingApplication {


    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) {
        SpringApplication.run(movieTicketBookingApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(CustomerRepository customerRepository,
                                               MovieRepository movieRepository) {
        return args -> {
            createRandomCustomer(customerRepository);
            createRandomMovie(movieRepository);
        };
    }

    private void createRandomCustomer(CustomerRepository customerRepository) {


        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password();
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        Customer customer = new Customer(
                customerName,
                customerEmail,
                password,
                customerPhone
        );
        customerRepository.save(customer);
    }

    private void createRandomMovie(MovieRepository movieRepository) {
        var movieName = FAKER.name().fullName();
        var rating = RANDOM.nextDouble(5);
        var cost = RANDOM.nextDouble(200, 400);

        Movie movie = new Movie(
                movieName,
                cost,
                rating
        );
        movieRepository.save(movie);
    }
}
