package com.naren.movieticketbookingapplication.Entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {

    @Test
    void testToString() {
        Customer customer = new Customer(1, "John Doe", "john@example.com", "password123", 1234567890L);
        assertEquals("Customer{customer_id=1, name='John Doe', email='john@example.com', password='password123', phoneNumber=1234567890}", customer.toString());
    }

    @Test
    void testEquals() {
        Customer customer1 = new Customer(1, "John Doe", "john@example.com", "password123", 1234567890L);
        Customer customer2 = new Customer(1, "John Doe", "john@example.com", "password123", 1234567890L);
        assertEquals(customer1, customer2);
    }

    @Test
    void testHashCode() {
        Customer customer1 = new Customer(1, "John Doe", "john@example.com", "password123", 1234567890L);
        Customer customer2 = new Customer(1, "John Doe", "john@example.com", "password123", 1234567890L);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }
}