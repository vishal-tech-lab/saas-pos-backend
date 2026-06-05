package com.example.Backend.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "orders"
    })
    private TableMaster table;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String paymentStatus; // PENDING, COMPLETED, CANCELLED

    @Column(nullable = false)
    private String orderStatus; // PENDING, CONFIRMED, PREPARING, READY, SERVED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime createdAt;


    @Column(name = "branchid", nullable = false)
private Long branchid;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "order"
    })
    private List<CustomerOrderItem> items;

  
}
