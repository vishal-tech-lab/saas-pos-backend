package com.example.Backend.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Service.Balanceservice;

@RestController 
@RequestMapping("calculate")
public class Balancecontroller {
   
    @Autowired
    private Balanceservice balanceservice;


   @GetMapping("dashboard/{customername}/{date}")
   public Map<String,Double> getDashboard(
    @PathVariable String customername,@PathVariable LocalDate date,
    @RequestParam(defaultValue = "false") boolean yesterdayBalance,
    @RequestParam(defaultValue = "false") boolean toadaySales,
    @RequestParam(defaultValue = "false") boolean todayPayments,
    @RequestParam(defaultValue = "false") boolean totalbalance
   ){
      return balanceservice.getDashboardData(customername, date, yesterdayBalance, toadaySales, todayPayments, totalbalance);
   }
   
   @GetMapping("dashboard/{date}")
   public Map<String,Double> getallbills(
    @RequestParam  String customername,@PathVariable LocalDate date,
    @RequestParam(defaultValue = "false") boolean yesterdayBalance,
    @RequestParam(defaultValue = "false") boolean toadaySales,
    @RequestParam(defaultValue = "false") boolean todayPayments,
    @RequestParam(defaultValue = "false") boolean totalbalance
   ){
      return balanceservice.getDashboardData(customername, date, yesterdayBalance, toadaySales, todayPayments, totalbalance);
   }
  
  @GetMapping("dashboard/customerbalance")
  public Double getcustomertotalbalance(){
   return balanceservice.getcustomertotalbalance();
  }

 @GetMapping("dashboard/customertable")
public List<Map<String, Object>> getCustomerTable() {
    return balanceservice.getCustomerBalances();
}

 @GetMapping("dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam(defaultValue = "today") String range) {
        Map<String, Object> response = balanceservice.dashboardResponse(range);
        return ResponseEntity.ok(response);
    }
 @GetMapping("dashboard/payment-expense-chart")
    public List<Map<String, Object>> getPaymentExpenseChart(
            @RequestParam(defaultValue = "today") String range) {
        return balanceservice.getPaymentExpenseChartByRange(range);
    }

 @GetMapping("dashboard/customerrecord/{date}")
  public List<Map<String, Object>> getcustomerrecord(@PathVariable LocalDate date){
    return balanceservice.getCustomerBalances(date);
  }
  @GetMapping("dashboard/top5customers")
public List<Map<String, Object>> getTop5Customers() {
    return balanceservice.getTop5CustomerBalances();
}
@GetMapping("dashboard/top5vegetables")
public List<String> getTop5Vegetables() {
    return balanceservice.getTop5VegetablesAllTime();
}
  }