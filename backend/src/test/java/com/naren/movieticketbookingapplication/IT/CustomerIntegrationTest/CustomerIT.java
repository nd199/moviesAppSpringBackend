package com.naren.movieticketbookingapplication.IT.CustomerIntegrationTest;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Entity.Role;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import com.naren.movieticketbookingapplication.Record.MovieRegistration;
import com.naren.movieticketbookingapplication.Repo.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;
    private static final Faker FAKER = new Faker();
    private static final String API_PATH = "/api/v1/customers";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CustomerRegistration registration;

    @BeforeEach
    void setUp() {
        createRoleIfNotExists();

        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = passwordEncoder.encode(FAKER.internet().password(8, 12));
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);

    }

    private void createRoleIfNotExists() {
        boolean existingRole = roleRepository.existsRoleByName("ROLE_USER");
        if (!existingRole) {
            Role role = new Role("ROLE_USER");
            webTestClient.post()
                    .uri("/api/v1/roles")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(role)
                    .exchange()
                    .expectStatus().isOk();
        }
    }

    @Test
    void createCustomer() {

        String JwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", JwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert customerDTOList != null;
        long customerId = customerDTOList.stream()
                .filter(c -> c.email().equals(registration.email()))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        CustomerDTO customerDTO = webTestClient.get()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", JwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(customerDTOList).contains(customerDTO);
    }

    @Test
    void addAdmin() {

        Role role = new Role("ROLE_ADMIN");
        webTestClient.post()
                .uri("/api/v1/roles")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Role added successfully");

        String JwtToken = webTestClient.post()
                .uri("api/v1/admins")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", JwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert customerDTOList != null;
        long customerId = customerDTOList.stream()
                .filter(c -> c.email().equals(registration.email()))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        CustomerDTO customerDTO = webTestClient.get()
                .uri("api/v1/customers" + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", JwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(customerDTOList).contains(customerDTO);
    }

    @Test
    void deleteACustomer() {

        Faker faker = new Faker();
        Name name = faker.name();
        var firstName = name.firstName();
        var lastName = name.lastName();
        var customer1 = firstName + lastName;
        var email = firstName + lastName + "@codeNaren.com";
        var firstName2 = name.firstName();
        var lastName2 = name.lastName();
        var customer2 = firstName2 + lastName2;
        var email2 = firstName2 + lastName2 + "@codeNaren.com";
        var password = passwordEncoder.encode(faker.internet().password(8, 12));
        Long customerPhone = Long.valueOf(faker.phoneNumber().subscriberNumber(9));
        Long customerPhone2 = Long.valueOf(faker.phoneNumber().subscriberNumber(9));

        CustomerRegistration request =
                new CustomerRegistration(customer1, email, password, customerPhone);

        // Posting Customer 1
        webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated();

        CustomerRegistration request2 =
                new CustomerRegistration(customer2, email2, password, customerPhone2);


        // Obtaining JWT Token after registering Customer 2
        String jwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Getting all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // Finding customer ID by email
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Deleting a customer
        webTestClient.delete()
                .uri(API_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk();

        // Attempting to retrieve the deleted customer by ID
        webTestClient.get()
                .uri(API_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isNotFound(); // Expecting 404 Not Found for a deleted customer
    }


    @Test
    void UpdateCustomer() {

        String jwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert customerDTOList != null;
        long customerId = customerDTOList.stream()
                .filter(c -> c.email().equals(registration.email()))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        String newName = "Naren";


        CustomerUpdateRequest update = new
                CustomerUpdateRequest(newName, registration.email(), registration.phoneNumber());


        webTestClient.put()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        CustomerDTO expected = webTestClient.get()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                }).returnResult()
                .getResponseBody();

        CustomerDTO actual = new CustomerDTO(
                customerId,
                newName, registration.email(), List.of("ROLE_USER"), registration.phoneNumber(),
                registration.email(),
                List.of()
        );

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("roles", "movies")
                .isEqualTo(expected);
    }

    @Test
    void testAddMovieToCustomer() {

        String jwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert customerDTOList != null;

        Long customerId = customerDTOList.stream()
                .filter(c -> c.email().equals(registration.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        MovieRegistration movie = new MovieRegistration("testName", 200.0, 2.0);

        webTestClient.post()
                .uri("api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(movie), MovieRegistration.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Movie> movieList = webTestClient.get()
                .uri("api/v1/movies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Movie>() {
                })
                .returnResult()
                .getResponseBody();

        assert movieList != null;
        Long movieId = movieList.stream()
                .filter(m -> m.getName().equals(movie.name()))
                .map(Movie::getMovie_id)
                .findFirst()
                .orElseThrow();

        webTestClient.put()
                .uri("/api/v1/customers/add-movie/{customerId}/{movieId}", customerId, movieId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk();

        CustomerDTO customerDTO = webTestClient.get()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        Movie expected = webTestClient.get()
                .uri("api/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Movie>() {
                })
                .returnResult()
                .getResponseBody();


        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.movies()).contains(expected);
    }

    @Test
    void testRemoveMovieToCustomer() {

        String jwtToken = webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assert customerDTOList != null;

        Long customerId = customerDTOList.stream()
                .filter(c -> c.email().equals(registration.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        // Register a new movie
        MovieRegistration movie = new MovieRegistration(FAKER.funnyName().name(), 200.0, 2.0);

        webTestClient.post()
                .uri("/api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(movie), MovieRegistration.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .returnResult()
                .getResponseBody();

        List<Movie> movieList = webTestClient.get()
                .uri("api/v1/movies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Movie>() {
                })
                .returnResult()
                .getResponseBody();

        assert movieList != null;
        Long movieId = movieList.stream()
                .filter(m -> m.getName().equals(movie.name()))
                .map(Movie::getMovie_id)
                .findFirst()
                .orElseThrow();

        Movie expected = webTestClient.get()
                .uri("/api/v1/movies/{id}", movieId)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Movie>() {
                })
                .returnResult()
                .getResponseBody();


        // Associate movie with customer
        webTestClient.put()
                .uri("/api/v1/customers/add-movie/{customerId}/{movieId}", customerId, movieId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk();

        // Remove movie from customer
        webTestClient.delete()
                .uri("/api/v1/customers/remove-movie/{customerId}/{movieId}", customerId, movieId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk();

        // Verify movie is removed from customer's movie list
        CustomerDTO customerDTO = webTestClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(customerDTO).isNotNull();
        assertThat(customerDTO.movies()).doesNotContain(expected);

    }

    @Test
    void testAddRole() {
        // Create a new role
        Role role = new Role("ROLE_TEST");

        // Send a POST request to add the role
        webTestClient.post()
                .uri("/api/v1/roles")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Role added successfully");

        // Retrieve the list of roles and verify that the new role is present
        List<Role> roles = webTestClient.get()
                .uri("/api/v1/roles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Role.class)
                .returnResult()
                .getResponseBody();

        assertThat(roles).isNotNull();
        assertThat(roles).extracting(Role::getName).contains("ROLE_TEST");
    }

    @Test
    void testDeleteRole() {

        Role role = new Role("ROLE_DELETE");

        webTestClient.post()
                .uri("/api/v1/roles")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(role), Role.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Role added successfully");

        // Retrieve the list of roles to get the ID of the role to delete
        List<Role> roles = webTestClient.get()
                .uri("/api/v1/roles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Role.class)
                .returnResult()
                .getResponseBody();

        assertThat(roles).isNotNull();
        Long id = roles.stream()
                .filter(r -> r.getName().equals("ROLE_DELETE"))
                .map(Role::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri("/api/v1/roles/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(roleRepository.findById(id)).isEmpty();
    }

}
