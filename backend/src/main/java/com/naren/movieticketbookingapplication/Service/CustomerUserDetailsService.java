package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.CustomerDao;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerDao customerDao;

    public CustomerUserDetailsService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user details for username: {}", username);

        Customer customer = customerDao.getCustomerByUsername(username)
                .orElseThrow(() -> {
                    String errorMessage = "Username " + username + " not found";
                    log.error(errorMessage);
                    return new UsernameNotFoundException(errorMessage);
                });

        log.info("User details loaded successfully for username: {}", username);
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(customer.getPassword())
                .roles(customer.getRoles().stream().map(Role::getName).toArray(String[]::new))
                .build();
    }
}
