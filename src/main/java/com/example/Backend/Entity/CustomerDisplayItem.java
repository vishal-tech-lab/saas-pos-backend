package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * CustomerDisplayItem Entity
 * Represents individual items displayed on customer display
 */
@Entity
@Table(name = "customer_display_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "display")
public class CustomerDisplayItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "displayid", nullable = false)
    private CustomerDisplay display;

    @Column(name = "itemname")
    private String itemname;

    @Column(name = "qty")
    private Double qty;

    @Column(name = "price")
    private Double price;

    @Column(name = "total")
    private Double total;
}
