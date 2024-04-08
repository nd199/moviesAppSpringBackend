package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Repo.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class CustomerDaoImpl implements CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerDaoImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void addCustomer(Customer customer) {
        log.info("Adding customer: {}", customer);
        customerRepository.save(customer);
        log.info("Customer added successfully: {}", customer);
    }

    @Override
    public Optional<Customer> getCustomer(Long customerId) {
        log.info("Fetching customer by ID: {}", customerId);
        Optional<Customer> customer = customerRepository.findById(customerId);
        log.info("Customer fetched: {}", customer.orElse(null));
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) {
        log.info("Updating customer: {}", customer);
        customerRepository.save(customer);
        log.info("Customer updated successfully: {}", customer);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.info("Checking if customer exists by email: {}", email);
        boolean exists = customerRepository.existsByEmail(email);
        log.info("Customer exists by email '{}': {}", email, exists);
        return exists;
    }

    @Override
    public boolean existsByPhoneNumber(Long phoneNumber) {
        log.info("Checking if customer exists by phone number: {}", phoneNumber);
        boolean exists = customerRepository.existsByPhoneNumber(phoneNumber);
        log.info("Customer exists by phone number '{}': {}", phoneNumber, exists);
        return exists;
    }

    @Override
    public List<Customer> getCustomerList() {
        log.info("Fetching list of customers");
        Page<Customer> page = customerRepository.findAll(Pageable.ofSize(1000));
        List<Customer> customers = page.getContent();
        log.info("Fetched {} customers", customers.size());
        return customers;
    }

    @Override
    public void deleteCustomer(Customer customer) {
        log.info("Deleting customer: {}", customer);
        customerRepository.delete(customer);
        log.info("Customer deleted successfully: {}", customer);
    }

    @Override
    public Optional<Customer> getCustomerByUsername(String email) {
        log.info("Fetching customer by username (email): {}", email);
        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        log.info("Customer fetched by username '{}': {}", email, customer.orElse(null));
        return customer;
    }
}
