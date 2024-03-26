package com.naren.movieticketbookingapplication.Dto;

import com.naren.movieticketbookingapplication.Entity.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDTOMapperTest {

    @Mock
    private CustomerDTOMapper customerDTOMapper;

    @Test
    void apply() {

        Customer customer = new Customer(1L, "test", "test@gmail.com", "o213123rd", 23232445L);
        CustomerDTO expected = new CustomerDTO(1L, "test", "test@gmail.com", 23232445L);
        when(customerDTOMapper.apply(customer)).thenReturn(expected);

        CustomerDTO actual = customerDTOMapper.apply(customer);


        assertNotNull(expected);
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.email()).isEqualTo(expected.email());
        assertThat(actual.phoneNumber()).isEqualTo(expected.phoneNumber());
        assertThat(actual).hasNoNullFieldsOrPropertiesExcept("password");
    }
}