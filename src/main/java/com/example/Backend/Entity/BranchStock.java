package com.example.Backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branch_stock")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BranchStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branchid", nullable = false)

    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })

    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productid", nullable = false)

    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })

    private Product product;

    @Column(nullable = false)
    private Double qty;
    
}