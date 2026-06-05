package com.example.Backend.Repository;

import com.example.Backend.Entity.CustomerOrder;
import com.example.Backend.Entity.TableMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    
    List<CustomerOrder> findByOrderStatus(String orderStatus);
    
    List<CustomerOrder> findByTable(TableMaster table);
    
    List<CustomerOrder> findByTableAndOrderStatus(TableMaster table, String orderStatus);
}
