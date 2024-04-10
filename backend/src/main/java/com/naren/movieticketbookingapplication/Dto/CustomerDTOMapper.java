package com.naren.movieticketbookingapplication.Dto;

import com.naren.movieticketbookingapplication.Entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerDTOMapper implements Function<Customer, CustomerDTO> {


    @Override
    public CustomerDTO apply(Customer customer) {
        log.debug("Mapping Customer to CustomerDTO: {}", customer);

        CustomerDTO customerDTO = new CustomerDTO(
                customer.getCustomer_id(),
                customer.getName(),
                customer.getEmail(),
                customer.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                customer.getPhoneNumber(),
                customer.getUsername(),
                customer.getMovies()
        );

        log.debug("Mapped Customer to CustomerDTO: {}", customerDTO);
        return customerDTO;
    }
}
