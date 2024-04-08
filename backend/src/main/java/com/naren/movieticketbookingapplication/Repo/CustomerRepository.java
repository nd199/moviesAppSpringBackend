package com.naren.movieticketbookingapplication.Repo;

import com.naren.movieticketbookingapplication.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(Long phoneNumber);

    Optional<Customer> findCustomerByEmail(String email);
}


/// dao -- lower level abstraction
// repo -> higher,

//dao -> boilerplate code
// repo -> reduce boilerplate code

// dao -> integration and configuration
// repo -> simplifies integration and configuration

