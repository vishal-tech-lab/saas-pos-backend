package com.example.Backend.Repository;

import com.example.Backend.Entity.Salesitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesitemRepository
        extends JpaRepository<Salesitem, Long> {

    List<Salesitem> findByBillno(
            String billno
    );

    List<Salesitem> findByCreatedatBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}