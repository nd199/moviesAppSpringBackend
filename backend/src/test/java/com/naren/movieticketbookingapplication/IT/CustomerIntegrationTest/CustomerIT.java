package com.naren.movieticketbookingapplication.IT.CustomerIntegrationTest;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
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
    private PasswordEncoder passwordEncoder;


    @Test
    void createCustomer() {

        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = passwordEncoder.encode(FAKER.internet().password(8, 12));
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        CustomerRegistration registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);


        String JwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isOk()
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
                .filter(c -> c.email().equals(customerEmail))
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
    void deleteACustomer() {

        Faker faker = new Faker();
        Name name = faker.name();
        var firstName = name.firstName();
        var lastName = name.lastName();
        var customer1 = firstName + lastName;
        var email = firstName + lastName + "@codeNaren.com";
        Name name2 = faker.name();
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
                .isOk();

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
                .isOk()
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
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        CustomerRegistration registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);


        String jwtToken = webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isOk()
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
                .filter(c -> c.email().equals(customerEmail))
                .map(CustomerDTO::id)
                .findFirst().orElseThrow();

        String newName = "Naren";


        CustomerUpdateRequest update = new
                CustomerUpdateRequest(newName, customerEmail, customerPhone);


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
                newName, customerEmail, List.of("ROLE_USER"), customerPhone, customerEmail
        );

        assertThat(actual).isEqualTo(expected);
    }
}
