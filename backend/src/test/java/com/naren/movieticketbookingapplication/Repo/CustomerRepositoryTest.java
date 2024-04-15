package com.naren.movieticketbookingapplication.Repo;

import com.naren.movieticketbookingapplication.AbstractTestContainers;
import com.naren.movieticketbookingapplication.Entity.Customer;
import com.naren.movieticketbookingapplication.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long phoneNumber = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));
        customer = new Customer(customerName, customerEmail, password, phoneNumber);
    }


    @Test
    void existsByEmail() {
        underTest.save(customer);
        var actual = underTest.existsByEmail(customer.getEmail());
        assertThat(actual).isTrue();
    }

    @Test
    void existsByPhoneNumber() {
        underTest.save(customer);
        var actual = underTest.existsByPhoneNumber(customer.getPhoneNumber());
        assertThat(actual).isTrue();
    }
}