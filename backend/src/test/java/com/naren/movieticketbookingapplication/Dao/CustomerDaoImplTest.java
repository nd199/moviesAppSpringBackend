package com.naren.movieticketbookingapplication.Dao;

import com.naren.movieticketbookingapplication.AbstractTestContainers;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.Repo.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerDaoImplTest extends AbstractTestContainers {


    private CustomerDaoImpl underTest;

    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    private static Customer getNewCustomer() {
        return new Customer(1, FAKER.name().name(), FAKER.internet().emailAddress(), FAKER.internet().password(), Long.valueOf(FAKER.phoneNumber().subscriberNumber(9)));
    }

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerDaoImpl(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void addCustomer() {
        Customer customer = getNewCustomer();

        underTest.addCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void getCustomer() {
        Customer customer = getNewCustomer();

        underTest.getCustomer(customer.getCustomer_id());

        verify(customerRepository).findById(customer.getCustomer_id());
    }

    @Test
    void updateCustomer() {
        Customer customer = getNewCustomer();

        underTest.updateCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void existsByEmail() {
        Customer customer = getNewCustomer();
        String email = customer.getEmail();

        underTest.existsByEmail(email);

        verify(customerRepository).existsByEmail(email);
    }

    @Test
    void existsByPhoneNumber() {
        Customer customer = getNewCustomer();
        Long phoneNumber = customer.getPhoneNumber();

        underTest.existsByPhoneNumber(phoneNumber);
        verify(customerRepository).existsByPhoneNumber(phoneNumber);
    }

    @Test
    void getCustomerList() {
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());

        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<Customer> expected = underTest.getCustomerList();

        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageableArgumentCaptor.capture());
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(1000));
    }

    @Test
    void deleteCustomer() {
        Customer customer = getNewCustomer();

        underTest.deleteCustomer(customer);

        verify(customerRepository).delete(customer);
    }
}