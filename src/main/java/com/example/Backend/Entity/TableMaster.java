package com.example.Backend.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "table_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    @Column(nullable = false)
    private String tableName;

    @Column(nullable = true)
    private String qrUrl;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE, RESERVED

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "table"
    })
    private List<CustomerOrder> orders;
@Column(name = "branchid", nullable = false)
private Long branchid;
   
}
