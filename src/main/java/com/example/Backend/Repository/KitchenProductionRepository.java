package com.example.Backend.Repository;

import com.example.Backend.Entity.KitchenProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenProductionRepository extends JpaRepository<KitchenProduction, Long> {
}
