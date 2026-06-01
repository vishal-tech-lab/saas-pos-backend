package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "register_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchid", nullable = false)
    private Branch branch;

private LocalDateTime openedat;

private LocalDateTime closedat;

private Boolean active;

private Double totalSales;

private Double cashSales;

private Double upiSales;

private Integer totalBills;
}