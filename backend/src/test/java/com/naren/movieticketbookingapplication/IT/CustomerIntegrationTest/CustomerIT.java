package com.naren.movieticketbookingapplication.IT.CustomerIntegrationTest;


import com.github.javafaker.Faker;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
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

    @Test
    void createCustomer() {

        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        CustomerRegistration registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);


        webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isOk();


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
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
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        CustomerRegistration registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);


        webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isOk();


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
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

        webTestClient.delete()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        webTestClient.get()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void UpdateCustomer() {
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        CustomerRegistration registration = new
                CustomerRegistration(customerName, customerEmail, password, customerPhone);


        webTestClient.post()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registration), CustomerRegistration.class)
                .exchange()
                .expectStatus()
                .isOk();


        List<CustomerDTO> customerDTOList = webTestClient.get()
                .uri(API_PATH)
                .accept(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        CustomerDTO expected = webTestClient.get()
                .uri(API_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                }).returnResult()
                .getResponseBody();

        CustomerDTO actual = new CustomerDTO(
                (long) customerId,
                newName, customerEmail, customerPhone
        );

        assertThat(actual).isEqualTo(expected);
    }
}
