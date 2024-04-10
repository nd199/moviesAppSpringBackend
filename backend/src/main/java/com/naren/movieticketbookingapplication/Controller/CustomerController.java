package com.naren.movieticketbookingapplication.Controller;

import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import com.naren.movieticketbookingapplication.Service.CustomerService;
import com.naren.movieticketbookingapplication.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    public CustomerController(CustomerService customerService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRegistration registration) {
        log.info("Creating customer...");
        customerService.createCustomer(registration);
        String token = jwtUtil.issueToken(registration.email(), "ROLE_USER");
        log.info("Token Created.");
        log.info("Token Sent Successfully.");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") Long customerId) {
        log.info("Fetching customer by ID: {}", customerId);
        CustomerDTO customerDTO = customerService.getCustomerById(customerId);
        log.info("Customer found: {}", customerDTO);
        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> customerList() {
        log.info("Fetching list of customers...");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        log.info("Fetched {} customers.", customers.size());
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@RequestBody CustomerUpdateRequest customer,
                                                   @PathVariable("id") Long customerId) {
        log.info("Updating customer with ID: {}", customerId);
        customerService.updateCustomer(customer, customerId);
        log.info("Customer updated successfully.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable("id") Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        log.info("Customer deleted successfully.");
    }

    @PutMapping("/customers/add-movie/{customerId}/{movieId}")
    public void addMovieToCustomer(@PathVariable Long customerId, @PathVariable Long movieId) {
        log.info("Adding movie with ID {} to customer with ID: {}", movieId, customerId);
        customerService.addMovieToCustomer(customerId, movieId);
        log.info("Movie with ID {} added to customer with ID: {}", movieId, customerId);
    }

    @PutMapping("/customers/remove-movie/{customerId}/{movieId}")
    public void removeMovieFromCustomer(@PathVariable Long customerId, @PathVariable Long movieId) {
        log.info("Removing movie with ID {} from customer with ID: {}", movieId, customerId);
        customerService.removeMovieFromCustomer(customerId, movieId);
        log.info("Movie with ID {} removed from customer with ID: {}", movieId, customerId);
    }
}