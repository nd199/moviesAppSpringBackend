package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;

import java.util.List;


public interface CustomerService {
    void createCustomer(CustomerRegistration registration);

    CustomerDTO getCustomerById(Integer customerId);

    void updateCustomer(CustomerUpdateRequest customer, Integer customerId);

    List<CustomerDTO> getAllCustomers();

    void deleteCustomer(Integer customerId);
}
