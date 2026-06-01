    package com.example.Backend.Entity;

    import jakarta.persistence.*;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "product")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long itemid;

        @Column(nullable = false)
        private String itemname;

        @Column(nullable = false)
        private Double price;

        @Column(nullable = false)
        private String category;

        @ManyToOne
        @JoinColumn(name = "branchid")
        @JsonIgnoreProperties({
                "hibernateLazyInitializer",
                "handler"
        })
        private Branch branch;
    }
