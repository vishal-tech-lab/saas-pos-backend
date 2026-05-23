package com.example.Backend.Repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Sales;
@Repository
public interface Salesrepo extends JpaRepository<Sales,Long> {
     
    Sales findByBillno(String billno);
    List<Sales>findByDateOrderByCustomername(LocalDate date);
    
    @Query("SELECT SUM(s.total) FROM Sales s WHERE LOWER(s.customername)= LOWER(:customername)")
    Double sumTotalByCustomer(@Param("customername") String customername);

   @Query("SELECT SUM(s.total) FROM Sales s WHERE LOWER(s.customername)= LOWER(:customername)  AND  s.date<:date")
   Double calculatetotalsalebudate(@Param ("customername")String customername,@Param("date") LocalDate date) ;
  
   @Query("SELECT COALESCE(SUM(s.total),0) FROM Sales s " +
       "WHERE LOWER(s.customername) LIKE LOWER(CONCAT('%', :customername, '%')) " +
       "AND s.date = :date")
   Double sumoftodaysales(@Param("customername") String customername, 
                       @Param("date") LocalDate date);

   @Query("SELECT s FROM Sales s WHERE LOWER(s.customername)=LOWER(:customername) AND s.billno=:billno")
   List<Object> slectvalues(@Param("customername") String customername,@Param("billno") String billno);

   @Query("SELECT s.billno from Sales s WHERE LOWER(s.customername) = LOWER(:customername) AND s.date=:date")
   List<String>  billnoByCustomername(@Param("customername") String customername,@Param("date") LocalDate date);
   @Query("SELECT s FROM Sales s  WHERE(:customername IS NULL OR s.customername=:customername) AND(:billno IS NULL OR s.billno=:billno)  AND(s.date BETWEEN:from AND :to)") 
   List<Sales> findsalesreport(@Param("customername") String customername,@Param("billno") String billno,@Param("from") LocalDate from,@Param("to") LocalDate to);
   
   @Query("SELECT s FROM Sales s WHERE LOWER(TRIM(s.customername)) = LOWER(TRIM(:customername)) AND s.date = :date")
    List<Sales> getItemsByCustomerAndDate(@Param("date") LocalDate date,
                                          @Param("customername") String customername);

   @Query("SELECT SUM(s.total) FROM Sales s WHERE s.date=:date")
   Double Sumtodaysale(@Param("date") LocalDate date);
  
   @Query("SELECT SUM(s.total) From Sales s ")
   Double FindallToatalSales();
  
   @Query("SELECT COALESCE(SUM(s.total),0) FROM Sales s  WHERE s.date BETWEEN :start AND :end")
   Double totalSalesInRange(@Param("start") LocalDate start,@Param("end") LocalDate end);
   
   @Query("SELECT p.date, SUM(p.total) " +
       "FROM Sales p " +
       "WHERE p.date BETWEEN :start AND :end " +
       "GROUP BY p.date " +
       "ORDER BY p.date ASC")
List<Object[]> getDailySales(@Param("start") LocalDate start,@Param("end") LocalDate end);

    @Query("SELECT COUNT(s) FROM Sales s WHERE s.date BETWEEN :start AND :end")
    Long countSalesInRange(@Param("start") LocalDate start,
                           @Param("end") LocalDate end);

    // ✅ Top 3 vegetables by frequency (not quantity, just name count)
    @Query("SELECT s.itemname " +
           "FROM Sales s " +
           "WHERE s.date BETWEEN :start AND :end " +
           "GROUP BY s.itemname " +
           "ORDER BY COUNT(s.itemname) DESC")
    List<String> findTopVegetables(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);
@Query("SELECT s.itemname FROM Sales s GROUP BY s.itemname ORDER BY COUNT(s.itemname) DESC")
List<String> findTopVegetablesAllTime();

}
