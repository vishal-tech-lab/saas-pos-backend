package com.example.Backend.Repository;

import com.example.Backend.Entity.Closeregister;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloseregisterRepository
        extends JpaRepository<Closeregister, Long> {

    Closeregister
    findTopByOrderByClosedatDesc();
}