package com.example.Backend.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "salesitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salesitem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salesitemid;

    @Column(nullable = false)
    private String billno;

    @Column(nullable = false)
    private String itemname;

    @Column(nullable = false)
    private Double qty;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = true)
    private Long customerid;

    @Column(nullable = true)
    private String paymentmethod;

    @ManyToOne
    @JoinColumn(name = "branchid")
    private Branch branch;

    @Column(nullable = false)
    private LocalDateTime createdat;
}

