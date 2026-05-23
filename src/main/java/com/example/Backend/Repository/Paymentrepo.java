package com.example.Backend.Repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Payment;
@Repository
public interface Paymentrepo extends JpaRepository<Payment, Long> {
    @Query("SELECT SUM(p.customerpayment) FROM Payment p WHERE LOWER(p.customername)= LOWER(:customername)")
    Double sumTotalByCustomer(@Param("customername") String customername);
   

    @Query("SELECT SUM(p.customerpayment) FROM Payment p  WHERE LOWER(p.customername)= LOWER(:customername)  AND  p.date<:date")
    Double calculatetotalpaymentbydate(@Param ("customername") String customername,@Param ("date") LocalDate date);
   
@Query("SELECT COALESCE(SUM(p.customerpayment),0) FROM Payment p " +
       "WHERE LOWER(p.customername) LIKE LOWER(CONCAT('%', :customername, '%')) " +
       "AND p.date = :date")
double sumoftotalpayment(@Param("customername") String customername,
                         @Param("date") LocalDate date);

   
     @Query("SELECT s FROM Payment s WHERE LOWER(s.customername)=LOWER(:customername) AND s.date=:date")
   List<Object> slectvalues(@Param("customername") String customername,@Param("date") LocalDate date);
    
   @Query(" SELECT SUM(p.customerpayment) FROM Payment p")
   Double FindallCustomerPayment();

   @Query("SELECT COALESCE(SUM(p.customerpayment),0) FROM Payment p  WHERE p.date BETWEEN :start and :end ")
   Double totalPaymentInRange(@Param ("start") LocalDate start,@Param ("end") LocalDate end);
   // Daily customer payments
@Query("SELECT p.date, SUM(p.customerpayment) " +
       "FROM Payment p " +
       "WHERE p.date BETWEEN :start AND :end " +
       "GROUP BY p.date " +
       "ORDER BY p.date ASC")
List<Object[]> getDailyCustomerPayments(@Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

@Query("SELECT p FROM Payment p " +
       "WHERE p.date BETWEEN :start AND :end " +
       "AND (:customername IS NULL OR LOWER(p.customername) = LOWER(:customername)) " +
       "ORDER BY p.date ASC")
List<Payment> findPaymentsInRangeWithCustomer(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("customername") String customername);

        
}

