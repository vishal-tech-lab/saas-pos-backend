package com.example.Backend.Repository;

import com.example.Backend.Entity.CustomerOrder;
import com.example.Backend.Entity.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {
    
    List<CustomerOrderItem> findByOrder(CustomerOrder order);
}
