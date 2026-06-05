package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CustomerDisplay Entity
 * Represents a customer facing display for a branch
 * One branch = One active customer display
 */
@Entity
@Table(name = "customer_display")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "items")
public class CustomerDisplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "displayid")
    private Long displayid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchid", nullable = false)
    private Branch branch;

    @Column(name = "billno")
    private String billno;

    @Column(name = "total")
    private Double total = 0.0;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "updatedat", updatable = false, insertable = true)
    private LocalDateTime updatedat = LocalDateTime.now();

    @OneToMany(
        mappedBy = "display",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<CustomerDisplayItem> items;

    @PreUpdate
    public void preUpdate() {
        this.updatedat = LocalDateTime.now();
    }
}
