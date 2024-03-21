package com.naren.movieticketbookingapplication.Repo;

import com.naren.movieticketbookingapplication.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(Long phoneNumber);
}


/// dao -- lower level abstraction
// repo -> higher,

//dao -> boilerplate code
// repo -> reduce boilerplate code

// dao -> integration and configuration
// repo -> simplifies integration and configuration

