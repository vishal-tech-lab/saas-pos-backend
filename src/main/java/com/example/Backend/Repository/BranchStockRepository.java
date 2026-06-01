package com.example.Backend.Repository;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchStockRepository extends JpaRepository<BranchStock, Long> {

    Optional<BranchStock> findByBranchAndProduct(Branch branch, Product product);
    
    @Query("""
SELECT COUNT(bs)
FROM BranchStock bs
WHERE bs.qty <= :limit
""")
Long getLowStockItems(
        @Param("limit") Double limit
);

    @Query("""
SELECT COUNT(bs)
FROM BranchStock bs
WHERE bs.qty <= :limit
  AND bs.branch = :branch
""")
Long getLowStockItems(
        @Param("limit") Double limit,
        @Param("branch") Branch branch
);

@Query("""
SELECT
    bs.product.itemname,
    SUM(bs.qty)
FROM BranchStock bs
GROUP BY bs.product.itemname
ORDER BY SUM(bs.qty) ASC
""")
List<Object[]> getStockStatus();

    @Query("""
SELECT
    bs.product.itemname,
    SUM(bs.qty)
FROM BranchStock bs
WHERE bs.branch = :branch
GROUP BY bs.product.itemname
ORDER BY SUM(bs.qty) ASC
""")
List<Object[]> getStockStatus(
        @Param("branch") Branch branch
    );
}
