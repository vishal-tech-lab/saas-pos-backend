package com.example.Backend.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

import com.example.Backend.Entity.Sales;
import com.example.Backend.Entity.SalesRequest;
import com.example.Backend.Service.Salesservice;

@RestController
@RequestMapping("sales")
public class Salescontroller {
     
       @Autowired
      private Salesservice salesservice;


       @PostMapping("register")
       public String savesales(@RequestBody SalesRequest sales){
              return salesservice.savesales(sales);
       }
       
       @GetMapping("total")
       public List<Sales> getallsales(){
         return salesservice.getallsales();
       }
       
       @GetMapping("items/{customername}/{billno}")
       public List<Object> gettheItems(@PathVariable String customername ,@PathVariable String billno){
        return salesservice.gettheItems(customername,billno);
       }

      @GetMapping("billno/{customername}/{date}")
          public List<String> getBillnoByCustomername(@PathVariable String customername,@PathVariable LocalDate date){
            return salesservice.getBillnoByCustomername(customername,date);
          }
     @PutMapping("update/{salesid}")
     public String upadateSales(@PathVariable Long salesid,@RequestBody Sales sales){
               return salesservice.upadatesales(salesid,sales);
     }
     @DeleteMapping("dalete/{salesid}")
     public String deletesales(@PathVariable Long salesid){
      return salesservice.deletesales(salesid); 
     }
    @GetMapping("/report") 
     public List<Sales> getsalesreport(@RequestParam(required = false) String customername,
                                       @RequestParam(required = false) String bilno,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
                                        return salesservice.getsalesreport(customername,bilno,from,to);
                                       }

 @GetMapping("/items/by-customer-date/{date}/{customername}")
    public List<Sales> getItemsByCustomerAndDate(
            @PathVariable LocalDate date,
            @PathVariable String customername) {
        // Trim here too for safety
        return salesservice.getItemsByCustomerAndDate(date, customername.trim());
    }

   @GetMapping("todaysales/{date}")
    public Double gettoadytotalsales(@PathVariable  LocalDate date){
        return salesservice.gettoadytotalsales(date);
                }
@GetMapping("count")        
public Long getcountofsales(){
  return salesservice.getcountofsales();
}                             

@GetMapping("bills/by-date/{date}")
public ResponseEntity <Map<String,List<Sales>>> getAllBillsForDate( @PathVariable LocalDate date){
  return ResponseEntity.ok(salesservice.getBillsByDate(date));

}
                                    }

