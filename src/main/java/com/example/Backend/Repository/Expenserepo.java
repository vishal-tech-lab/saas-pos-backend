package com.example.Backend.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.Expense;
@Repository
public interface Expenserepo  extends JpaRepository<Expense,Long>{
 
    @Query("SELECT  SUM(e.amount) FROM Expense e")
    Double findalllamount();

    @Query("SELECT COALESCE(SUM(s.amount),0) FROM  Expense s WHERE s.date BETWEEN :start AND :end ") 
   Double findTotalExpenseInRange(@Param("start") LocalDate  start,@Param("end") LocalDate end);

   @Query("SELECT e.date, SUM(e.amount) " +
       "FROM Expense e " +
       "WHERE e.date BETWEEN :start AND :end " +
       "GROUP BY e.date " +
       "ORDER BY e.date ASC")
    List<Object[]> getDailyExpenses(@Param("start") LocalDate start,
                                @Param("end") LocalDate end);
                                    
     @Query("""
SELECT COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date = :today
""")
Double getTodayExpense(
    LocalDate today
);

     @Query("""
SELECT COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date = :today
  AND e.branch = :branch
""")
Double getTodayExpense(
    LocalDate today,
    @Param("branch") Branch branch
);

    @Query("""
SELECT
    e.category,
    COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
GROUP BY e.category
ORDER BY SUM(e.amount) DESC
""")
    List<Object[]> getExpenseCategories(
            @Param("startDate")
            LocalDate startDate
    );

    @Query("""
SELECT
    e.category,
    COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
  AND e.branch = :branch
GROUP BY e.category
ORDER BY SUM(e.amount) DESC
""")
    List<Object[]> getExpenseCategories(
            @Param("startDate") LocalDate startDate,
            @Param("branch") Branch branch
    );

    @Query("""
SELECT
    e.category,
    COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
GROUP BY e.category
ORDER BY SUM(e.amount) DESC
""")
    List<Object[]> getExpenseSplit(
            @Param("startDate")
            LocalDate startDate
    );

    @Query("""
SELECT
    e.category,
    COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
  AND e.branch = :branch
GROUP BY e.category
ORDER BY SUM(e.amount) DESC
""")
    List<Object[]> getExpenseSplit(
            @Param("startDate") LocalDate startDate,
            @Param("branch") Branch branch
    );

@Query("""
SELECT COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
""")
Double getTotalExpenses(
        @Param("startDate")
        LocalDate startDate
);

@Query("""
SELECT COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.date >= :startDate
  AND e.branch = :branch
""")
Double getTotalExpenses(
        @Param("startDate") LocalDate startDate,
        @Param("branch") Branch branch
);

List<Expense> findByDate(LocalDate date);
}