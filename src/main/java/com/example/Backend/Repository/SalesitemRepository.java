package com.example.Backend.Repository;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.Salesitem;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesitemRepository
        extends JpaRepository<Salesitem, Long> {

    List<Salesitem> findByBillno(
            String billno
    );

    List<Salesitem> findByBillnoAndBranch(
            String billno,
            Branch branch
    );

    List<Salesitem> findByBranch(
            Branch branch
    );

    List<Salesitem> findByBranchAndCreatedatBetween(
            Branch branch,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Salesitem> findByCreatedatBetween(
            LocalDateTime start,
            LocalDateTime end
    );

   

    @Query("""
        SELECT COALESCE(SUM(s.total),0)
        FROM Salesitem s
        WHERE s.createdat >= :start
          AND s.createdat < :end
    """)
    Double getTodaySales(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(s.total),0)
        FROM Salesitem s
        WHERE s.createdat >= :start
          AND s.createdat < :end
          AND s.branch = :branch
    """)
    Double getTodaySales(
            LocalDateTime start,
            LocalDateTime end,
            @Param("branch") Branch branch
    );

    @Query("""
        SELECT COUNT(DISTINCT s.billno)
        FROM Salesitem s
        WHERE s.createdat >= :start
          AND s.createdat < :end
    """)
    Long getTodayOrders(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT COUNT(DISTINCT s.billno)
        FROM Salesitem s
        WHERE s.createdat >= :start
          AND s.createdat < :end
          AND s.branch = :branch
    """)
    Long getTodayOrders(
            LocalDateTime start,
            LocalDateTime end,
            @Param("branch") Branch branch
    );

    @Query("""
SELECT
    DATE(s.createdat),
    COALESCE(SUM(s.total),0)
FROM Salesitem s
WHERE s.createdat >= :startDate
GROUP BY DATE(s.createdat)
ORDER BY DATE(s.createdat)
""")
List<Object[]> getSalesChart(
        LocalDateTime startDate
);

    @Query("""
SELECT
    DATE(s.createdat),
    COALESCE(SUM(s.total),0)
FROM Salesitem s
WHERE s.createdat >= :startDate
  AND s.branch = :branch
GROUP BY DATE(s.createdat)
ORDER BY DATE(s.createdat)
""")
List<Object[]> getSalesChart(
        @Param("startDate") LocalDateTime startDate,
        @Param("branch") Branch branch
);

@Query("""
SELECT
    s.itemname,
    SUM(s.qty)
FROM Salesitem s
GROUP BY s.itemname
ORDER BY SUM(s.qty) DESC
""")
List<Object[]> getTopSellingProducts();

    @Query("""
SELECT
    s.itemname,
    SUM(s.qty)
FROM Salesitem s
WHERE s.branch = :branch
GROUP BY s.itemname
ORDER BY SUM(s.qty) DESC
""")
List<Object[]> getTopSellingProducts(
        @Param("branch") Branch branch
    );

    @Query("""
SELECT COALESCE(SUM(s.total),0)
FROM Salesitem s
WHERE s.createdat >= :startDate
""")
    Double getTotalSales(
            LocalDateTime startDate
    );

    @Query("""
SELECT COALESCE(SUM(s.total),0)
FROM Salesitem s
WHERE s.createdat >= :startDate
  AND s.branch = :branch
""")
    Double getTotalSales(
            LocalDateTime startDate,
            @Param("branch") Branch branch
    );
}
