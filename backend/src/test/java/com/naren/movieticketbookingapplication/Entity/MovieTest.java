package com.naren.movieticketbookingapplication.Entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MovieTest {

    @Test
    void testToString() {
        Movie movie = new Movie(1, "test", 100.99, 4.5);

        String result = movie.toString();

        assertEquals("Movie{movie_id=1, name='test', cost=100.99, rating=4.5}", result);
    }

    @Test
    void testEquals() {
        Movie movie1 = new Movie(1, "test", 100.99, 4.5);
        Movie movie2 = new Movie(1, "test", 100.99, 4.5);
        Movie movie3 = new Movie(2, "test", 120.99, 4.8);
        assertEquals(movie1, movie2);
        assertNotEquals(movie1, movie3);
    }

    @Test
    void testHashCode() {
        Movie movie1 = new Movie(1, "test", 100.99, 4.5);
        Movie movie2 = new Movie(1, "test", 100.99, 4.5);
        Movie movie3 = new Movie(2, "test", 120.99, 4.8);
        assertEquals(movie1.hashCode(), movie2.hashCode());
        assertNotEquals(movie1.hashCode(), movie3.hashCode());
    }
}