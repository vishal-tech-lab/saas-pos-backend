package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Expensecategory;
@Repository
public interface Expensecategoryrepo extends JpaRepository<Expensecategory,Long> {
    
}
