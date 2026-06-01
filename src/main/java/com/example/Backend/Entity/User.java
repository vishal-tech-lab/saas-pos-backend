package com.example.Backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false, unique = true)
    private String username;

    @com.fasterxml.jackson.annotation.JsonProperty(
            access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
    )
    @jakarta.persistence.Column(nullable = false)
    private String password;

    @jakarta.persistence.Column(nullable = false)
    private String role; // ROLE_ADMIN / ROLE_CASHIER / ROLE_KITCHEN

    @jakarta.persistence.Column(nullable = false)
    private String status; // PENDING / APPROVED / REJECTED

    @ManyToOne
    @JoinColumn(name = "branchid")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Branch branch;
}