package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Customer;

@Repository
public interface Customerrepo extends JpaRepository<Customer,Long>  {
    
}
