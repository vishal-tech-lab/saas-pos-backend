package com.example.Backend.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Backend.Entity.Sales;

public interface DashboardRepository extends JpaRepository <Sales,Long> {
    
    @Query("""
            SELECT s.customername,SUM(s.total)-COALESCE(SUM(p.customerpayment),0)
            FROM Sales s
            LEFT JOIN Payment p ON s.customername=p.customername
            GROUP BY s.customername

            """)
    List<Object[]>findCustomerTotalBalances();    







   
}

 
