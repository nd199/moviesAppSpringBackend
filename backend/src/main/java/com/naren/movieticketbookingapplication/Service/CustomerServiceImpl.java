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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;

    private final CustomerDTOMapper customerDTOMapper;

    public CustomerServiceImpl(CustomerDao customerDao, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
    }

    private static final int REQ_PASSWORD_LENGTH = 8;

    private static boolean validatePassword(String password, String name, String email, Long phoneNumber) {
        if (password == null || password.length() < REQ_PASSWORD_LENGTH) {
            System.out.println(password);
            throw new PasswordInvalidException("Password must be at least " + REQ_PASSWORD_LENGTH + " characters long.");
        }
        if (containsPersonalInfo(password, name, email, phoneNumber)) {
            throw new PasswordInvalidException("Password must not contain name, email, or phone number.");
        }
        return true;
    }

    private static boolean containsPersonalInfo(String password, String name, String email, Long phoneNumber) {
        return password.contains(name) || password.contains(email) || password.contains(String.valueOf(phoneNumber));
    }


    @Override
    public void createCustomer(CustomerRegistration registration) {

        boolean isValid = validatePassword(registration.password(), registration.name(), registration.email(), registration.phoneNumber());

        if (!isValid) throw new PasswordInvalidException("Password must not contain name/phoneNumber or personal info");

        else {
            if (customerDao.existsByEmail(registration.email())) {
                throw new ResourceAlreadyExists(
                        "Email already taken"
                );
            } else if (customerDao.existsByPhoneNumber(registration.phoneNumber())) {
                throw new ResourceAlreadyExists(
                        "Phone number already taken"
                );
            }
            Customer customer = getCustomer(registration);
            customerDao.addCustomer(customer);
        }
    }


    private Customer getCustomer(CustomerRegistration registration) {
        return new Customer(
                registration.name(),
                registration.email(),
                registration.password(),
                registration.phoneNumber()
        );
    }

    @Override
    public CustomerDTO getCustomerById(Integer customerId) {
        return customerDao.getCustomer(customerId).map(customerDTOMapper).
                orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));
    }


    @Override
    public void updateCustomer(CustomerUpdateRequest request, Integer id) {

        Customer customer = customerDao.getCustomer(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                )
        );

        boolean changes = false;

        if (request.name() != null && !request.name().equals(customer.getName())) {
            customer.setName(request.name());
            changes = true;
        }
        if (request.email() != null && !request.email().equals(customer.getEmail())) {
            if (customerDao.existsByEmail(request.email())) {
                throw new ResourceAlreadyExists(
                        "Email already taken"
                );
            }
            customer.setEmail(request.email());
            changes = true;
        }
        if (request.phone() != null && !request.phone().equals(customer.getPhoneNumber())) {
            customer.setPhoneNumber(request.phone());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }


    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerDao.getCustomerList()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        Customer customer = customerDao.getCustomer(customerId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                )
        );
        customerDao.deleteCustomer(customer);
    }
}
