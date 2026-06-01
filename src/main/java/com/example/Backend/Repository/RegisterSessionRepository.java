package com.example.Backend.Repository;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.RegisterSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisterSessionRepository
        extends JpaRepository<RegisterSession, Long> {

    Optional<RegisterSession>
    findByBranchAndActiveTrue(
            Branch branch
    );

    Optional<RegisterSession>
    findTopByOrderByClosedatDesc();
}