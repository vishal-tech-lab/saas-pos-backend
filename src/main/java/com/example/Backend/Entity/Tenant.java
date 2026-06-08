package com.example.Backend.Entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity

@Table(
        name = "tenants",
        schema = "public"
)
public class Tenant {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

private String companyName;

private String schemaName;

private String username;

@Column(nullable = false)
private String password;
private String subdomain;

}