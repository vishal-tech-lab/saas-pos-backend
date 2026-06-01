package com.example.Backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Expensecategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   expensecategoryid;
    private String expensecategory;

        @ManyToOne
        @JoinColumn(name = "branchid")
        @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
        })
        private Branch branch;

}
