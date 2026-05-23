package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "closeregister")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Closeregister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long closeregisterid;

    private LocalDateTime closedat;
}