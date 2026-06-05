package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for table status update
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableStatusDto {
    private String status; // ACTIVE, INACTIVE, RESERVED, OCCUPIED
}
