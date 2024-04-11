package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.CustomerDao;
import com.naren.movieticketbookingapplication.Dao.MovieDao;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Dto.CustomerDTOMapper;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Entity.Movie;
import com.naren.movieticketbookingapplication.Entity.Role;
import com.naren.movieticketbookingapplication.Exception.*;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import com.naren.movieticketbookingapplication.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Transactional
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final long REQ_PASSWORD_LENGTH = 8;
    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final RoleService roleService;
    private final MovieDao movieDao;
    private final JwtUtil jwtUtil;

    public CustomerServiceImpl(CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, RoleService roleService, MovieDao movieDao, JwtUtil jwtUtil) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.roleService = roleService;
        this.movieDao = movieDao;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void addRole(Role role) {
        if (role == null) throw new ResourceNotFoundException("Role cannot be null");
        if(roleService.existsByName(role)) throw new ResourceAlreadyExists("Role already exists");
        roleService.saveRole(role);
    }

    @Override
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    @Override
    public ResponseEntity<?> registerUser(CustomerRegistration customerRegistration,
                                          Set<String> roleNames) {

        Customer registeredCustomer = registerCustomer(customerRegistration);

        Set<Role> roles = new HashSet<>();

        for (String roleName : roleNames) {
            Role role = roleService.findRoleByName(roleName);
            if (role == null) {
                return ResponseEntity.badRequest().body("Role " + roleName + " not found");
            }
            roles.add(role);
        }

        roles.forEach(registeredCustomer::addRole);

        customerDao.addCustomer(registeredCustomer);

        String token = jwtUtil.issueToken(registeredCustomer.getUsername(), roles);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, token)
                .body("Customer registered successfully!");
    }


    private Customer registerCustomer(CustomerRegistration customerRegistration) {

        boolean isValidPassword = validatePassword(customerRegistration.password(),
                customerRegistration.name(),
                customerRegistration.email(),
                customerRegistration.phoneNumber());
        if (!isValidPassword) {
            throw new PasswordInvalidException("Invalid password");
        }

        if (customerDao.existsByEmail(customerRegistration.email())) {
            throw new ResourceAlreadyExists("Email already taken");
        }
        if (customerDao.existsByPhoneNumber(customerRegistration.phoneNumber())) {
            throw new ResourceAlreadyExists("Phone number already taken");
        }
        return new Customer(customerRegistration.name(),
                customerRegistration.email(),
                passwordEncoder.encode(customerRegistration.password()),
                customerRegistration.phoneNumber());
    }

    private boolean validatePassword(String password, String name, String email, Long phoneNumber) {
        if (password == null || password.length() < REQ_PASSWORD_LENGTH) {
            log.error("Password must be at least {} characters long", REQ_PASSWORD_LENGTH);
            return false;
        }
        return !containsPersonalInfo(password, name, email, phoneNumber);
    }

    private boolean containsPersonalInfo(String password, String name, String email, Long phoneNumber) {
        return password.contains(name) || password.contains(email) || password.contains(String.valueOf(phoneNumber));
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

        log.info("Customer updated successfully: {}", customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("Fetching all customers");

        List<CustomerDTO> customers = customerDao.getCustomerList()
                .stream()
                .map(customerDTOMapper)
                .toList();
        log.info("Retrieved {} customers", customers.size());

        return customers;
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);

        Customer customer = customerDao.getCustomer(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));

        customerDao.deleteCustomer(customer);
        log.info("Customer deleted successfully: {}", customer);
    }

    @Override
    public void addMovieToCustomer(Long customerId, Long movieId) {
        log.info("Adding movie with ID {} to customer with ID {}", movieId, customerId);

        Customer customer = customerDao.getCustomer(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
        Movie movie = movieDao.getMovieById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with ID " + movieId + " not found"));

        if (customer.getMovies().contains(movie)) {
            throw new Customer_MovieAlreadyExistsException(
                    "Customer %s already subscribed to %s movie".formatted(customer, movie));
        }
        customer.addMovie(movie);
        customerDao.updateCustomer(customer);

        log.info("Movie added to customer successfully: Customer={}, Movie={}", customer.getName(), movie.getName());
    }

    @Override
    public void removeMovieFromCustomer(Long customerId, Long movieId) {
        log.info("Removing movie with ID {} from customer with ID {}", movieId, customerId);

        Customer customer = customerDao.getCustomer(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + customerId + " not found"));
        Movie movie = movieDao.getMovieById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with ID " + movieId + " not found"));

        if (!customer.getMovies().contains(movie)) {
            throw new Customer_MovieNotFound(
                    "Customer %s not subscribed to %s movie".formatted(customer, movie));
        }
        customer.removeMovie(movie);
        customerDao.updateCustomer(customer);
        log.info("Movie removed from customer successfully: Customer={}, Movie={}", customer.getName(), movie.getName());
    }
}
