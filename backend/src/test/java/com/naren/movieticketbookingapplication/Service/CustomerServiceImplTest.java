package com.naren.movieticketbookingapplication.Service;

import com.naren.movieticketbookingapplication.Dao.CustomerDao;
import com.naren.movieticketbookingapplication.Dto.CustomerDTOMapper;
import com.naren.movieticketbookingapplication.Dto.CustomerDTO;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Exception.PasswordInvalidException;
import com.naren.movieticketbookingapplication.Exception.RequestValidationException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerServiceImpl(customerDao, customerDTOMapper);
    }

    @Test
    void createCustomer() {

        String email = "test@example.com";


        CustomerRegistration registration = new CustomerRegistration("testName", email, "testpassword", 20220292232L);

        when(customerDao.existsByEmail(email)).thenReturn(false);
        when(customerDao.existsByPhoneNumber(registration.phoneNumber())).thenReturn(false);

        underTest.createCustomer(registration);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).addCustomer(customerArgumentCaptor.capture());

        Customer customer = customerArgumentCaptor.getValue();

        assertThat(customer.getCustomer_id()).isNull();
        assertThat(customer.getName()).isEqualTo(registration.name());
        assertThat(customer.getPassword()).isEqualTo(registration.password());
        assertThat(customer.getEmail()).isEqualTo(registration.email());
        assertThat(customer.getPhoneNumber()).isEqualTo(registration.phoneNumber());
    }

    @Test
    void ThrowsIfCustomerWithPersonalInfoInPassword() {

        CustomerRegistration registration = new CustomerRegistration("testName", "testEmail", "testName123", 1234567890L);

        assertThatThrownBy(() -> underTest.createCustomer(registration))
                .isInstanceOf(PasswordInvalidException.class)
                .hasMessage("Password must not contain name/phoneNumber or personal info");
    }

    @Test
    void throwsIfEmailAlreadyTaken() {
        String email = "test@example.com";

        when(customerDao.existsByEmail(email)).thenReturn(true);
        CustomerRegistration registration = new CustomerRegistration("testName", email, "testpassword", 20220292232L);

        assertThatThrownBy(() -> underTest.createCustomer(registration)).isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void throwsIfMobileAlreadyTaken() {
        Long mobile = 20220292232L;

        when(customerDao.existsByPhoneNumber(mobile)).thenReturn(true);

        CustomerRegistration registration = new CustomerRegistration("testName", "testEmail@gmail.com", "testpassword", 20220292232L);

        assertThatThrownBy(() -> underTest.createCustomer(registration)).isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Phone number already taken");

        verify(customerDao, never()).addCustomer(any());
    }

    @Test
    void getCustomerById() {
        int id = 1;

        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", "password", 223123453L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        CustomerDTO actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void willThrowIfCustomerWithIdNotExist() {
        int id = 1;

        when(customerDao.getCustomer(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void updateCustomer() {
        int id = 2;

        Customer customer = new Customer(id, "testName", "testEmail", "testpassword", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest("testName2", "testEmail2", 202202922L);

        when(customerDao.existsByEmail(request.email())).thenReturn(false);

        underTest.updateCustomer(request, id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer expected = customerArgumentCaptor.getValue();

        assertThat(expected.getEmail()).isEqualTo(request.email());
        assertThat(expected.getName()).isEqualTo(request.name());
        assertThat(expected.getPhoneNumber()).isEqualTo(request.phone());
    }

    @Test
    void WillThrowIfTryingToUpdateCustomerIdNotExist() {
        int id = 2;

        when(customerDao.getCustomer(id)).thenReturn(Optional.empty());

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("testName", "testEmail", 20220292232L);

        assertThatThrownBy(() -> underTest.updateCustomer(updateRequest, id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void WillThrowIfNewEmailAlreadyExistsWhileUpdating() {
        int id = 2;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("alex", "a@gmail.com", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(new Customer()));
        when(customerDao.existsByEmail(updateRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(updateRequest, id)).isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).updateCustomer(any());
    }


    @Test
    void canUpdateOnlyCustomerName() {
        int id = 2;

        Customer customer = new Customer(id, "testName", "testEmail", "testpassword", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest("testName2", "testEmail", 20220292232L);

        underTest.updateCustomer(request, id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer expected = customerArgumentCaptor.getValue();

        assertThat(expected.getEmail()).isEqualTo(request.email());
        assertThat(expected.getName()).isEqualTo(request.name());
        assertThat(expected.getPhoneNumber()).isEqualTo(request.phone());
    }

    @Test
    void canUpdateOnlyCustomerPhoneNumber() {
        int id = 2;

        Customer customer = new Customer(id, "testName", "testEmail", "testpassword", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest("testName2", "testEmail", 202202232L);

        underTest.updateCustomer(request, id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer expected = customerArgumentCaptor.getValue();

        assertThat(expected.getEmail()).isEqualTo(request.email());
        assertThat(expected.getName()).isEqualTo(request.name());
        assertThat(expected.getPhoneNumber()).isEqualTo(request.phone());
    }

    @Test
    void canUpdateCustomerEmail() {

        int id = 2;

        Customer customer = new Customer(id, "testName", "testEmail", "testPassword", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest("testName", "testEmail2", 20220292232L);

        when(customerDao.existsByEmail(request.email())).thenReturn(false);

        underTest.updateCustomer(request, id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer expected = customerArgumentCaptor.getValue();

        assertThat(expected.getEmail()).isEqualTo(request.email());
        assertThat(expected.getName()).isEqualTo(request.name());
        assertThat(expected.getPhoneNumber()).isEqualTo(request.phone());
    }


    @Test
    void throwsIfNoChangeExists() {

        int id = 2;

        Customer customer = new Customer(id, "testName", "testEmail", "testPassword", 20220292232L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest("testName", "testEmail", 20220292232L);

        assertThatThrownBy(() -> underTest.updateCustomer(request, id)).isInstanceOf(RequestValidationException.class).hasMessage("no data changes found");

        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void getAllCustomers() {

        underTest.getAllCustomers();

        verify(customerDao).getCustomerList();
    }

    @Test
    void deleteCustomer() {
        int id = 1;
        Customer customer = new Customer(id, "alex", "alex@gmail.com", "password", 2233322999L);

        when(customerDao.getCustomer(id)).thenReturn(Optional.of(customer));

        underTest.deleteCustomer(id);

        verify(customerDao).deleteCustomer(customer);
    }

    @Test
    void ThrowsWhenTryingToDeleteACustomer() {
        int id = 1;

        when(customerDao.getCustomer(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteCustomer(id)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomer(any());
    }
}