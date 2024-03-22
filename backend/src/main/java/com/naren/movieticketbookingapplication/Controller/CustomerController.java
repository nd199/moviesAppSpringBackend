package com.naren.movieticketbookingapplication.Controller;

import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import com.naren.movieticketbookingapplication.Service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRegistration registration) {
        log.info("Creating customer...");
        customerService.createCustomer(registration);
        log.info("Customer created successfully.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") Integer customerId) {
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
                                                   @PathVariable("id") Integer customerId) {
        log.info("Updating customer with ID: {}", customerId);
        customerService.updateCustomer(customer, customerId);
        log.info("Customer updated successfully.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable("id") Integer customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        log.info("Customer deleted successfully.");
    }
}