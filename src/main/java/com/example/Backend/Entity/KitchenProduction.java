package com.example.Backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_production")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KitchenProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionid;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "branchid")

    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })

    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "productid")

    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })

    private Product product;

    private Double qty;

    private LocalDateTime productiondate;

    private String notes;
}