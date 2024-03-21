package com.naren.movieticketbookingapplication.Repo;

import com.naren.movieticketbookingapplication.AbstractTestContainers;
import com.naren.movieticketbookingapplication.Entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void existsByEmail() {
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password();
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        Customer customer = new Customer(customerName, customerEmail, password, customerPhone);

        underTest.save(customer);

        var existsByEmail = underTest.existsByEmail(customerEmail);

        assertThat(existsByEmail).isTrue();
    }

    @Test
    void existsByPhoneNumber() {
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password();
        Long customerPhone = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));

        Customer customer = new Customer(customerName, customerEmail, password, customerPhone);

        underTest.save(customer);

        var existsByPhoneNumber = underTest.existsByPhoneNumber(customerPhone);

        assertThat(existsByPhoneNumber).isTrue();
    }

}