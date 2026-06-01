package com.example.Backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfer")

@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "frombranchid", nullable = false)
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tobranchid", nullable = false)
    private Branch toBranch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productid", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Double qty;

    @Column(nullable = false)
    private LocalDateTime transferdate;
}