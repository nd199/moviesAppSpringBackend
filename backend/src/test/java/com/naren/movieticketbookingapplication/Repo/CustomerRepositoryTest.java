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
        var password = FAKER.internet().password(8, 12);
        Long phoneNumber = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));
        Customer customer = new Customer(customerName, customerEmail, password, phoneNumber);
        underTest.save(customer);
        var actual = underTest.existsByEmail(customerEmail);
        assertThat(actual).isTrue();
    }

    @Test
    void existsByPhoneNumber() {
        var customerName = FAKER.name().name();
        var customerEmail = customerName + "@codeNaren.com";
        var password = FAKER.internet().password(8, 12);
        Long phoneNumber = Long.valueOf(FAKER.phoneNumber().subscriberNumber(9));
        Customer customer = new Customer(customerName, customerEmail, password, phoneNumber);
        underTest.save(customer);
        var actual = underTest.existsByPhoneNumber(phoneNumber);
        assertThat(actual).isTrue();
    }
}