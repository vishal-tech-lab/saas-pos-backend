package com.example.Backend.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Sales;
import com.example.Backend.Entity.SalesRequest;
import com.example.Backend.Repository.Salesrepo;


@Service
public class Salesservice {

    @Autowired
    private Salesrepo salesrepo;


    public String savesales(SalesRequest request) {
     for(Sales sales:request.getItems()){
        sales.setBillno(request.getBillno());
        sales.setDate(request.getDate());
        sales.setCustomername(request.getCustomername());
        salesrepo.save(sales)  ;
     }
             return "SALES ADDED SUCESSFULLY "; 

}

    public List<Sales> getallsales() {
     return  salesrepo.findAll();
    }

    public String   upadatesales(Long salesid,Sales sales){
 Sales existingbill = salesrepo.findById(salesid)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        existingbill.setCustomername(sales.getCustomername());   
        existingbill.setDate(sales.getDate());     
        existingbill.setItemname(sales.getItemname());
        existingbill.setBag(sales.getBag());     
        existingbill.setItemprice(sales.getTotal());     
        existingbill.setWeight(sales.getWeight());     
        existingbill.setDate(sales.getDate());     


         salesrepo.save(existingbill);
         return "Item Updated Sucessfully";
    }

    public double toatalpaymentbycustomer(String customername){
      Double sum=  salesrepo.sumTotalByCustomer(customername);
       return sum!=null?sum:0.0;
    }

    public List<Object> gettheItems(String customername, String billno) {
        return salesrepo.slectvalues(customername,billno);     
    }

    public List<String>  getBillnoByCustomername(String customername,LocalDate date) {
       return  salesrepo.billnoByCustomername(customername,date);
    }

    public String deletesales(Long salesid) {
        salesrepo.deleteById(salesid);
        return "Sucessfully updated";
           
    }

    public List<Sales> getsalesreport(String customername, String bilno, LocalDate from, LocalDate to) {
        return salesrepo.findsalesreport(customername, bilno, from, to);
    }

  public List<Sales> getItemsByCustomerAndDate(LocalDate date, String customername) {
        // Trim input to avoid mismatch
        return salesrepo.getItemsByCustomerAndDate(date, customername.trim());}

    
    public Double gettoadytotalsales(LocalDate date){
        return salesrepo.Sumtodaysale(date);
    }

    public Long getcountofsales(){
      return  salesrepo.count();
    }
    
    public Map<String,List<Sales>> getBillsByDate(LocalDate date){
        List<Sales> saleslist=salesrepo.findByDateOrderByCustomername(date);
        return saleslist.stream().collect(Collectors.groupingBy(Sales::getCustomername));
    }
}
