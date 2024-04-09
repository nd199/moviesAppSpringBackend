package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;

import java.util.List;


public interface CustomerService {
    void createCustomer(CustomerRegistration registration);

    CustomerDTO getCustomerById(Long customerId);

    void updateCustomer(CustomerUpdateRequest customer, Long customerId);

    List<CustomerDTO> getAllCustomers();

    void deleteCustomer(Long customerId);

    void addMovieToCustomer(Long customerId, Long movieId);

    void removeMovieFromCustomer(Long customerId, Long movieId);

}
