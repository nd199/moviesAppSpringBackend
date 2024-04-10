package com.naren.movieticketbookingapplication.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "Movie", uniqueConstraints = {
        @UniqueConstraint(name = "movie_name_unique",
                columnNames = "name")
})
@Setter
@Getter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "movie_id")
    @SequenceGenerator(name = "movie_id",
            sequenceName = "movie_id",
            allocationSize = 1)
    private Long movie_id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    public Movie(Long movie_id, String name, Double cost, Double rating) {
        this.movie_id = movie_id;
        this.name = name;
        this.cost = cost;
        this.rating = rating;
    }

    public Movie(String name, Double cost, Double rating) {
        this.name = name;
        this.cost = cost;
        this.rating = rating;
    }

    @Override
    public String
    toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(movie_id, movie.movie_id) && Objects.equals(name, movie.name) && Objects.equals(cost, movie.cost) && Objects.equals(rating, movie.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movie_id, name, cost, rating);
    }
}
