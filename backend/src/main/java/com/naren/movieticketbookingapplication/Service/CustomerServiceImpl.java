package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.CustomerDao;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Dto.CustomerDTOMapper;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Exception.PasswordInvalidException;
import com.naren.movieticketbookingapplication.Exception.RequestValidationException;
import com.naren.movieticketbookingapplication.Exception.ResourceAlreadyExists;
import com.naren.movieticketbookingapplication.Exception.ResourceNotFoundException;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerServiceImpl(CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    private static final long REQ_PASSWORD_LENGTH = 8;

    private boolean validatePassword(String password, String name, String email, Long phoneNumber) {
        if (password == null || password.length() < REQ_PASSWORD_LENGTH) {
            log.error("Password must be at least {} characters long", REQ_PASSWORD_LENGTH);
            throw new PasswordInvalidException("Password must be at least " + REQ_PASSWORD_LENGTH + " characters long.");
        }
        if (containsPersonalInfo(password, name, email, phoneNumber)) {
            log.error("Password must not contain name, email, or phone number");
            throw new PasswordInvalidException("Password must not contain name, email, or phone number.");
        }
        return true;
    }

    private static boolean containsPersonalInfo(String password, String name, String email, Long phoneNumber) {
        return password.contains(name) || password.contains(email) || password.contains(String.valueOf(phoneNumber));
    }

    @Override
    public void createCustomer(CustomerRegistration registration) {
        log.info("Creating customer: {}", registration.email());

        boolean isValid = validatePassword(registration.password(), registration.name(), registration.email(), registration.phoneNumber());

        if (isValid) {
            if (customerDao.existsByEmail(registration.email())) {
                log.error("Email {} already exists", registration.email());
                throw new ResourceAlreadyExists("Email already taken");
            } else if (customerDao.existsByPhoneNumber(registration.phoneNumber())) {
                log.error("Phone number {} already exists", registration.phoneNumber());
                throw new ResourceAlreadyExists("Phone number already taken");
            }

            Customer customer = getCustomer(registration);
            customerDao.addCustomer(customer);
        }
    }

    private Customer getCustomer(CustomerRegistration registration) {
        return new Customer(
                registration.name(),
                registration.email(),
                passwordEncoder.encode(registration.password()),
                registration.phoneNumber()
        );
    }

    @Override
    public CustomerDTO getCustomerById(Long customerId) {
        log.info("Fetching customer by ID: {}", customerId);

        return customerDao.getCustomer(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
    }

    @Override
    public void updateCustomer(CustomerUpdateRequest request, Long id) {
        log.info("Updating customer with ID: {}", id);

        Customer customer = customerDao.getCustomer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + id + " not found"));

        boolean changes = false;

        if (request.name() != null && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            changes = true;
        }
        if (request.email() != null && !request.email().equals(customer.getEmail())) {
            if (customerDao.existsByEmail(request.email())) {
                log.error("Email {} already exists", request.email());
                throw new ResourceAlreadyExists("Email already taken");
            }
            customer.setEmail(request.email());
            changes = true;
        }
        if (request.phoneNumber() != null && !request.phoneNumber().equals(customer.getPhoneNumber())) {
            customer.setPhoneNumber(request.phoneNumber());
            changes = true;
        }

        if (!changes) {
            log.warn("No data changes found for customer with ID: {}", id);
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("Fetching all customers");

        return customerDao.getCustomerList()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        Customer customer = customerDao.getCustomer(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));

        customerDao.deleteCustomer(customer);
    }
}
