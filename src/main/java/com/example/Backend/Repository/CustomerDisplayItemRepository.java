package com.example.Backend.Repository;

import com.example.Backend.Entity.CustomerDisplayItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CustomerDisplayItemRepository
 * Repository for CustomerDisplayItem entity
 */
@Repository
public interface CustomerDisplayItemRepository extends JpaRepository<CustomerDisplayItem, Long> {
}
