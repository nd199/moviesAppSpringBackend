package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Customer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    void addCustomer(Customer customer);

    Optional<Customer> getCustomer(Long customerId);

    void updateCustomer(Customer customer);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(Long phoneNumber);

    List<Customer> getCustomerList();

    void deleteCustomer(Customer customer);

    Optional<Customer> getCustomerByUsername(String email);
}


