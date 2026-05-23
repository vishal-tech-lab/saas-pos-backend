    package com.example.Backend.Controller;

import java.time.LocalDate;
import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

    import com.example.Backend.Entity.Payment;
    import com.example.Backend.Service.Paymentservice;

    @RestController
    @RequestMapping("payment")
    public class Paymentcontroller {
        
        @Autowired
        private Paymentservice paymentservice;

        @PostMapping("register")
        public String savepayment(@RequestBody Payment payment){
            return paymentservice.savepayment(payment);
        }
    
        @GetMapping("paymentss")
        public List<Payment> getallpayments(){
            return paymentservice.getallpayments();
        }
        
        @GetMapping("totalpayment/{customername}")
        public Map<String,Double> gettotalpaymentbycustomer(@PathVariable  String customername){
        Double payment= paymentservice.sumtoatlofcustomer(customername);
        Map<String, Double> response = new HashMap<>();
            response.put("totalPayments", payment);
            return response;                                   
        }   
        
        @PutMapping("update/{paymentid}")
        public Payment updatethepayments(@PathVariable Long paymentid,@RequestBody  Payment payment){
            return paymentservice.updatethepayments(paymentid,payment);
        }

        @DeleteMapping("delete/{paymentid}")
        public String deleteThePayments(@PathVariable Long papaymentid){
         return paymentservice.deleteThePayments(papaymentid);
        }
        @GetMapping("{customername}/{date}")
   public List<Object> sumoftotalpayment(@PathVariable String customername,@PathVariable LocalDate date){
    return paymentservice.sumoftotalpayment(customername, date);
   }
   @GetMapping("/payments")
    public List<Payment> getPayments(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "customername", required = false) String customername) {

        return paymentservice.getFilteredPayments(from, to, customername);
    }

    }

