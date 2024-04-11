package com.naren.movieticketbookingapplication.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    Set<Customer> customers = new HashSet<Customer>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    public Role(Long id, String name, Set<Customer> customers) {
        this.id = id;
        this.name = name;
        this.customers = customers;
    }

    public Role(String name, Set<Customer> customers) {
        this.name = name;
        this.customers = customers;
    }

    public Role(String name) {
        this.name = name;
    }
}
