package com.example.Backend.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Payment;
import com.example.Backend.Repository.Paymentrepo;
@Service
public class Paymentservice {
    @Autowired
   private Paymentrepo paymentrepo;
    public List<Payment> getonlyPayments;

     public String savepayment(Payment payment) {
        paymentrepo.save(payment);
        return "SUCESSFULLY SAVED";   
     }

    public List<Payment> getallpayments() {
        return  paymentrepo.findAll();
    }

   public double sumtoatlofcustomer(String customername){
     Double sum=paymentrepo.sumTotalByCustomer(customername);
     return sum !=null ?sum:0.0;
   }

   public Payment updatethepayments(Long paymentid, Payment payment ) {
      return paymentrepo.findById(paymentid).map((paymnets)->{paymnets.setCustomerpayment(payment.getCustomerpayment());
         return paymentrepo.save(paymnets);
      })
      .orElseThrow(()-> new RuntimeException("payments not found "));
   }
      
   public List<Object> sumoftotalpayment(String customername,LocalDate date)
   {
      return paymentrepo.slectvalues(customername,date);
   }

   public String deleteThePayments(Long papaymentid) {
      paymentrepo.deleteById(papaymentid);
      return ("payment deleted sucessfullly");
      
   }
public List<Payment> getFilteredPayments(LocalDate start, LocalDate end, String customerName) {
        // If the frontend sends an empty string, convert it to null 
        // so the Repository Query (:customername IS NULL) works correctly.
        if (customerName != null && customerName.trim().isEmpty()) {
            customerName = null;
        }
        
        return paymentrepo.findPaymentsInRangeWithCustomer(start, end, customerName);
    }


   
}
