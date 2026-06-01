 package com.example.Backend.Entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Expense {
   @Id
   @GeneratedValue(strategy =GenerationType.IDENTITY )
   private Long Expenseid;

    private LocalDate date;
    private String category;
    private Double amount;
    private String description;

    @ManyToOne
    @JoinColumn(name = "branchid", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Branch branch;
}