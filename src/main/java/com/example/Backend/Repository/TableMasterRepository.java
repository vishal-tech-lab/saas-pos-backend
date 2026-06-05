package com.example.Backend.Repository;

import com.example.Backend.Entity.TableMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TableMasterRepository extends JpaRepository<TableMaster, Long> {
    
    List<TableMaster> findByStatus(String status);
    
    Optional<TableMaster> findByTableName(String tableName);
}
