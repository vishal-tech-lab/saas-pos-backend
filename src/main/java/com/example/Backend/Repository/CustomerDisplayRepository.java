package com.example.Backend.Repository;

import com.example.Backend.Entity.CustomerDisplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * CustomerDisplayRepository
 * Repository for CustomerDisplay entity
 */
@Repository
public interface CustomerDisplayRepository extends JpaRepository<CustomerDisplay, Long> {

    /**
     * Find customer display by branch id
     * @param branchid the branch id
     * @return Optional containing CustomerDisplay if found
     */
    Optional<CustomerDisplay> findByBranch_Branchid(Long branchid);
}
