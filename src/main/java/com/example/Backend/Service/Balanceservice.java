package com.example.Backend.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import com.example.Backend.Entity.Customer;
import com.example.Backend.Entity.Payment;
import com.example.Backend.Entity.Sales;
import com.example.Backend.Repository.DashboardRepository;
    import com.example.Backend.Repository.Expenserepo;
    import com.example.Backend.Repository.Paymentrepo;
    import com.example.Backend.Repository.Salesrepo;
    @Service
    public class Balanceservice {
        @Autowired
        private Paymentrepo paymentrepo;
        @Autowired
        private Salesrepo salesrepo;     
        @Autowired
        private DashboardRepository dashboardrepository;
        @Autowired
        private Customerservice customerservice;
        @Autowired
        private Expenserepo expenserepo;
    
    public Map<String,Double> getDashboardData(String customername, LocalDate date,
    boolean yesterdayBalance,boolean toadaySales,boolean todayPayments,boolean totalbalance){
        Map<String,Double> result=new HashMap<>();
        if(yesterdayBalance){
            Double sales=salesrepo.calculatetotalsalebudate(customername, date);
            Double payment=paymentrepo.calculatetotalpaymentbydate(customername, date);
                result.put("yesterdayBalance", (sales == null ? 0.0 : sales) - (payment == null ? 0.0 : payment));
        }
        if(toadaySales){
        Double todaysales= salesrepo.sumoftodaysales(customername,date);
        result.put("todayssale", todaysales);
        }

        if(todayPayments){
            Double todaypayments=paymentrepo.sumoftotalpayment(customername, date);
            result.put("todayspayments", todaypayments);
        }

        if(totalbalance){
            Double sales=salesrepo.sumTotalByCustomer(customername);
            Double payment =paymentrepo.sumTotalByCustomer(customername);
            result.put("totalbalance", (sales == null ? 0.0 : sales) - (payment == null ? 0.0 : payment));
        }

        return result;
    }

    public Double getcustomertotalbalance() {
        Double sales=salesrepo.FindallToatalSales();
        Double payment =paymentrepo.FindallCustomerPayment();
        double customerbalance=sales-payment;
        return(customerbalance);
    }

    public List<Object[]> findCustomerTotalBalances(){
        return dashboardrepository.findCustomerTotalBalances();
    }
    public List<Map<String, Object>> getCustomerBalances() {
    List<Customer> customers=customerservice.getallcustomer();
        List<Map<String, Object>> result = new ArrayList<>();

        for(Customer c: customers){
            Double sales=salesrepo.sumTotalByCustomer(c.getCustomername());
            Double payment =paymentrepo.sumTotalByCustomer(c.getCustomername());
            double balance = (sales == null ? 0.0 : sales) - (payment == null ? 0.0 : payment);
        
                Map<String, Object> map = new HashMap<>();
                map.put("customername", c.getCustomername());
                map.put("balance", balance);
                result.add(map);
            }
        return result;

    }


  public List<Map<String, Object>> getCustomerBalances(LocalDate date) {
        // Get distinct customer names from Sales + Payments
        List<String> salesCustomers = salesrepo.findAll()
                .stream().map(Sales::getCustomername).distinct().toList();
        List<String> paymentCustomers = paymentrepo.findAll()
                .stream().map(Payment::getCustomername).distinct().toList();

        Set<String> allCustomers = new HashSet<>();
        allCustomers.addAll(salesCustomers);
        allCustomers.addAll(paymentCustomers);

        List<Map<String, Object>> table = new ArrayList<>();

        int index = 1;
        for (String customer : allCustomers) {
            // ✅ Old Balance = (total sales till yesterday) - (total payments till yesterday)
            Double totalSalesBefore = salesrepo.calculatetotalsalebudate(customer, date);
            Double totalPaymentsBefore = paymentrepo.calculatetotalpaymentbydate(customer, date);
            double oldBalance = 
                (totalSalesBefore != null ? totalSalesBefore : 0.0) -
                (totalPaymentsBefore != null ? totalPaymentsBefore : 0.0);

            // ✅ Today's Sales
            Double todaySales = salesrepo.sumoftodaysales(customer, date);
            todaySales = (todaySales != null ? todaySales : 0.0);

            // ✅ Today's Payments
            Double todayPayments = paymentrepo.sumoftotalpayment(customer, date);
            todayPayments = (todayPayments != null ? todayPayments : 0.0);

            // ✅ Final Balance
            double finalBalance = oldBalance + todaySales - todayPayments;

            // Build row
            Map<String, Object> row = new HashMap<>();
            row.put("id", index++);
            row.put("customerName", customer);
            row.put("oldBalance", oldBalance);
            row.put("todaySales", todaySales);
            row.put("todayPayments", todayPayments);
            row.put("finalBalance", finalBalance);

            table.add(row);
        }

        return table;
    }
public Map<String, Object> dashboardResponse(String range) {
    LocalDate end   = LocalDate.now();
    LocalDate start = calculateStart(range, end);

    double sales   = Optional.ofNullable(salesrepo.totalSalesInRange(start, end)).orElse(0.0);
    double payment = Optional.ofNullable(paymentrepo.totalPaymentInRange(start, end)).orElse(0.0);
    double expense = Optional.ofNullable(expenserepo.findTotalExpenseInRange(start, end)).orElse(0.0);

    double revenue = payment - (expense + sales);
    double customerBalance = payment - sales;

    // ✅ Get extra values
    Long salesCount = salesrepo.countSalesInRange(start, end);
    


    // ✅ Response Map
    Map<String, Object> response = new HashMap<>();
    response.put("totalSales", sales);
    response.put("customerPayment", payment);
    response.put("expense", expense);
    response.put("revenue", revenue);
    response.put("customerBalance", customerBalance);
    response.put("salesCount", salesCount);

    return response;
}

public List<String> getTop5VegetablesAllTime() {
    return salesrepo.findTopVegetablesAllTime()
                    .stream()
                    .limit(5)
                    .toList();
}
    public LocalDate calculateStart(String range ,LocalDate end){
        return switch(range.toLowerCase()){
        case "today"     -> end;
        case "thisweek"  -> end.with(DayOfWeek.MONDAY);
        case "thismonth" -> end.withDayOfMonth(1);
        case "last1month"-> end.minusMonths(1);
        case "last6month"-> end.minusMonths(6);
        case "last1year" -> end.minusYears(1);
        default          -> end.minusDays(1);
        };
    }

    public List<Map<String, Object>> getPaymentExpenseChart(LocalDate start, LocalDate end) {
        // Step 1: Get daily payments and expenses from repositories
        List<Object[]> payments = paymentrepo.getDailyCustomerPayments(start, end);
        List<Object[]> expenses = expenserepo.getDailyExpenses(start, end);
        List<Object[]> sales = salesrepo.getDailySales(start, end);

        // Step 2: Merge payments and expenses by date
        Map<LocalDate, Map<String, Double>> merged = new TreeMap<>(); // TreeMap sorts by date

        // Process payments
        for (Object[] row : payments) {
            LocalDate date = (LocalDate) row[0];
            Double payment = ((Number) row[1]).doubleValue();
            merged.putIfAbsent(date, new HashMap<>());
            merged.get(date).put("customerPayment", payment);
        }

        // Process expenses
        for (Object[] row : expenses) {
            LocalDate date = (LocalDate) row[0];
            Double expense = ((Number) row[1]).doubleValue();
            merged.putIfAbsent(date, new HashMap<>());
            merged.get(date).put("expense", expense);
        }
        //process sales
             for (Object[] row : sales) {
            LocalDate date = (LocalDate) row[0];
            Double sale = ((Number) row[1]).doubleValue();
            merged.putIfAbsent(date, new HashMap<>());
            merged.get(date).put("sales", sale);}

        // Step 3: Convert merged map to List<Map<String,Object>> for frontend
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Double>> entry : merged.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey().toString());
            map.put("customerPayment", entry.getValue().getOrDefault("customerPayment", 0.0));
            map.put("expense", entry.getValue().getOrDefault("expense", 0.0));
            map.put("sales", entry.getValue().getOrDefault("sales", 0.0));
            chartData.add(map);
        }

        return chartData;    
    }

    
    // ------------------------
    // 2️⃣ Get payments & expenses based on range like "today", "thisweek"
    // ------------------------
    public List<Map<String, Object>> getPaymentExpenseChartByRange(String range) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;

        switch (range.toLowerCase()) {
            case "today":
                start = today;
                break;
            case "thisweek":
                start = today.with(DayOfWeek.MONDAY);
                break;
            case "thismonth":
                start = today.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case "last1month":
                start = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                end = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "last6month":
                start = today.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
                break;
            case "last1year":
                start = today.minusYears(1).with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                start = today; // fallback to today if invalid
                break;
        }

        // Reuse the first method to get chart data
        return getPaymentExpenseChart(start, end);
    }
    public List<Map<String, Object>> getTop5CustomerBalances() {
    List<Customer> customers = customerservice.getallcustomer();
    List<Map<String, Object>> result = new ArrayList<>();

    for(Customer c: customers){
        Double sales = salesrepo.sumTotalByCustomer(c.getCustomername());
        Double payment = paymentrepo.sumTotalByCustomer(c.getCustomername());
        double balance = (sales == null ? 0.0 : sales) - (payment == null ? 0.0 : payment);

        Map<String, Object> map = new HashMap<>();
        map.put("customername", c.getCustomername());
        map.put("balance", balance);
        result.add(map);
    }

    // 🔥 SORT by balance DESCENDING
    result.sort((a, b) -> Double.compare(
        (double)b.get("balance"),
        (double)a.get("balance")
    ));

    // 🔥 TAKE TOP 5
    return result.stream().limit(5).toList();
}
    } 


