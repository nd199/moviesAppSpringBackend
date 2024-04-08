package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.CustomerDao;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Dto.CustomerDTOMapper;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Exception.PasswordInvalidException;
import com.naren.movieticketbookingapplication.Exception.ResourceAlreadyExists;
import com.naren.movieticketbookingapplication.Exception.ResourceNotFoundException;
import com.naren.movieticketbookingapplication.Record.CustomerRegistration;
import com.naren.movieticketbookingapplication.Record.CustomerUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerServiceImpl(customerDao, passwordEncoder, customerDTOMapper);
    }

    @Test
    void createCustomer_ValidRegistration_Success() {
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        CustomerRegistration registration = new CustomerRegistration("testName", email, password, 20220292232L);

        when(customerDao.existsByEmail(email)).thenReturn(false);
        when(customerDao.existsByPhoneNumber(registration.phoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        underTest.createCustomer(registration);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).addCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo("testName");
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getPassword()).isEqualTo(encodedPassword);
        assertThat(capturedCustomer.getPhoneNumber()).isEqualTo(20220292232L);
    }

    @Test
    void createCustomer_PersonalInfoInPassword_ThrowsException() {
        CustomerRegistration registration = new CustomerRegistration("testName", "testEmail", "testName123", 1234567890L);

        assertThatThrownBy(() -> underTest.createCustomer(registration))
                .isInstanceOf(PasswordInvalidException.class)
                .hasMessage("Password must not contain name, email, or phone number.");

        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void createCustomer_InvalidPasswordLength_ThrowsException() {
        CustomerRegistration registration = new CustomerRegistration("testName", "test@example.com", "pass", 20220292232L);

        assertThatThrownBy(() -> underTest.createCustomer(registration))
                .isInstanceOf(PasswordInvalidException.class)
                .hasMessage("Password must be at least 8 characters long.");

        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void createCustomer_EmailAlreadyExists_ThrowsException() {
        String email = "test@example.com";
        when(customerDao.existsByEmail(email)).thenReturn(true);

        CustomerRegistration registration = new CustomerRegistration("testName", email, "testpassword", 20220292232L);

        assertThatThrownBy(() -> underTest.createCustomer(registration))
                .isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void getCustomerById_ExistingCustomerId_ReturnsCustomerDTO() {
        long customerId = 1;
        Customer customer = new Customer(customerId, "Alex", "alex@example.com", "password", 1234567890L);
        when(customerDao.getCustomer(customerId)).thenReturn(Optional.of(customer));

        CustomerDTO result = underTest.getCustomerById(customerId);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Alex");
        assertThat(result.email()).isEqualTo("alex@example.com");
        assertThat(result.phoneNumber()).isEqualTo(1234567890L);
    }

    @Test
    void getCustomerById_NonExistingCustomerId_ThrowsException() {
        long nonExistingCustomerId = 100;
        when(customerDao.getCustomer(nonExistingCustomerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(nonExistingCustomerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID 100 not found");

        verify(customerDao).getCustomer(nonExistingCustomerId);
    }

    @Test
    void updateCustomer_ValidUpdateRequest_SuccessfullyUpdatesCustomer() {
        long customerId = 1;
        Customer customer = new Customer(customerId, "testName", "test@example.com", "oldPassword", 20220292232L);
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("newName", "new@example.com", 9999999999L);

        when(customerDao.getCustomer(customerId)).thenReturn(Optional.of(customer));
        when(customerDao.existsByEmail(updateRequest.email())).thenReturn(false);

        underTest.updateCustomer(updateRequest, customerId);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer updatedCustomer = customerArgumentCaptor.getValue();
        assertThat(updatedCustomer.getName()).isEqualTo("newName");
        assertThat(updatedCustomer.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedCustomer.getPhoneNumber()).isEqualTo(9999999999L);
    }

    @Test
    void updateCustomer_NonExistingCustomerId_ThrowsException() {
        long nonExistingCustomerId = 100;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("newName", "new@example.com", 9999999999L);

        when(customerDao.getCustomer(nonExistingCustomerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateCustomer(updateRequest, nonExistingCustomerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID 100 not found");

        verify(customerDao).getCustomer(nonExistingCustomerId);
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void deleteCustomer_ExistingCustomerId_SuccessfullyDeletesCustomer() {
        long customerId = 1;
        Customer customer = new Customer(customerId, "testName", "test@example.com", "password", 20220292232L);
        when(customerDao.getCustomer(customerId)).thenReturn(Optional.of(customer));

        underTest.deleteCustomer(customerId);

        verify(customerDao).deleteCustomer(customer);
    }

    @Test
    void deleteCustomer_NonExistingCustomerId_ThrowsException() {
        long nonExistingCustomerId = 100;
        when(customerDao.getCustomer(nonExistingCustomerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteCustomer(nonExistingCustomerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with ID 100 not found");

        verify(customerDao).getCustomer(nonExistingCustomerId);
        verify(customerDao, never()).deleteCustomer(any());
    }
}