package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.Entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    void addCustomer(Customer customer);

    Optional<Customer> getCustomer(Integer customerId);

    void updateCustomer(Customer customer);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(Long phoneNumber);

    List<Customer> getCustomerList();

    void deleteCustomer(Customer customer);
}


