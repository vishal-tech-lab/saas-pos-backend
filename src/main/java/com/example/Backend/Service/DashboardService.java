package com.example.Backend.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.Backend.Dto.DashboardSummaryDto;
import com.example.Backend.Dto.ExpenseCategoryDto;
import com.example.Backend.Dto.ProfitLossDto;
import com.example.Backend.Dto.SalesChartDto;
import com.example.Backend.Dto.StockStatusDto;
import com.example.Backend.Dto.TopSellingProductDto;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.User;
import com.example.Backend.Repository.BranchStockRepository;
import com.example.Backend.Repository.Expenserepo;
import com.example.Backend.Repository.SalesitemRepository;
import com.example.Backend.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final SalesitemRepository salesitemRepository;

    private final Expenserepo expenseRepository;

    private final BranchStockRepository branchStockRepository;
    
    private final UserRepository userRepository;

    @Value("${stock.low.limit}")
    private Double lowStockLimit;
    
    private Branch getUserBranch() {
        var authentication = 
            SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = 
            authentication.getName();

        User user = 
            userRepository
                .findByUsername(username);

        return user.getBranch();
    }
    
    public DashboardSummaryDto getSummary() {

        LocalDate today = LocalDate.now();

        LocalDateTime start =
                today.atStartOfDay();

        LocalDateTime end =
                today.plusDays(1)
                        .atStartOfDay();

        Branch branch = getUserBranch();

        Double todaySales =
                salesitemRepository
                        .getTodaySales(start, end, branch);

        Long todayOrders =
                salesitemRepository
                        .getTodayOrders(start, end, branch);

        Double todayExpense =
                expenseRepository
                        .getTodayExpense(today, branch);

        if (todaySales == null) {
            todaySales = 0.0;
        }

        if (todayExpense == null) {
            todayExpense = 0.0;
        }

        Double todayProfit =
                todaySales - todayExpense;

        Long lowStockItems =
                branchStockRepository
                        .getLowStockItems(
                                lowStockLimit,
                                branch
                        );

        return new DashboardSummaryDto(
                todaySales,
                todayOrders,
                todayProfit,
                lowStockItems
        );
    }
    
    public List<SalesChartDto>
getSalesChart(int days) {

    LocalDateTime startDate =
            LocalDate.now()
                     .minusDays(days)
                     .atStartOfDay();

    Branch branch = getUserBranch();

    return salesitemRepository
            .getSalesChart(startDate, branch)
            .stream()
            .map(row -> new SalesChartDto(
                    row[0].toString(),
                    ((Number) row[1]).doubleValue()
            ))
            .toList();
}

public List<TopSellingProductDto>
getTopSellingProducts() {

    Branch branch = getUserBranch();
    
    log.info("Fetching top selling products for branch: {}", 
        branch != null ? branch.getBranchname() : "null");

    List<Object[]> results = 
        salesitemRepository
            .getTopSellingProducts(branch);
    
    log.info("Query returned {} results", results.size());

    return results
            .stream()
            .limit(5)
            .map(row -> {
                String itemName = (String) row[0];
                Double qty = ((Number) row[1]).doubleValue();
                log.debug("Product: {} - Qty: {}", itemName, qty);
                return new TopSellingProductDto(itemName, qty);
            })
            .toList();
}

    public List<StockStatusDto>
getStockStatus() {

        Branch branch = getUserBranch();

        return branchStockRepository
                .getStockStatus(branch)
                .stream()
                .map(row -> {

                    Double qty =
                        ((Number) row[1])
                        .doubleValue();

                    String status;

                    if (qty <= 20) {

                        status = "LOW";

                    } else if (qty <= 50) {

                        status = "MEDIUM";

                    } else {

                        status = "HEALTHY";
                    }

                    return new StockStatusDto(
                            (String) row[0],
                            qty,
                            status
                    );
                })
                .toList();
}

    public List<ExpenseCategoryDto>
getLast7DaysExpenses() {

        LocalDate startDate =
                LocalDate.now()
                         .minusDays(7);

        Branch branch = getUserBranch();

        List<Object[]> results =
                expenseRepository
                        .getExpenseCategories(
                                startDate,
                                branch
                        );

        Double totalAmount =
                results.stream()
                       .mapToDouble(
                           row -> ((Number) row[1])
                                  .doubleValue()
                       )
                       .sum();

        return results.stream()
                .map(row -> {

                    String category =
                            (String) row[0];

                    Double amount =
                            ((Number) row[1])
                                    .doubleValue();

                    Double percentage =
                            totalAmount == 0
                            ? 0
                            : (amount * 100)
                              / totalAmount;

                    return new ExpenseCategoryDto(
                            category,
                            amount,
                            percentage
                    );
                })
                .toList();
}

    public List<ExpenseCategoryDto>
getLast30DaysExpenseSplit() {

        LocalDate startDate =
                LocalDate.now()
                         .minusDays(30);

        Branch branch = getUserBranch();

        List<Object[]> results =
                expenseRepository
                        .getExpenseSplit(
                                startDate,
                                branch
                        );

        Double totalAmount =
                results.stream()
                       .mapToDouble(
                           row -> ((Number) row[1])
                                  .doubleValue()
                       )
                       .sum();

        return results.stream()
                .map(row -> {

                    String category =
                            (String) row[0];

                    Double amount =
                            ((Number) row[1])
                                    .doubleValue();

                    Double percentage =
                            totalAmount == 0
                            ? 0
                            : (amount * 100)
                              / totalAmount;

                    return new ExpenseCategoryDto(
                            category,
                            amount,
                            percentage
                    );
                })
                .toList();
}

    public ProfitLossDto getProfitLoss(
            int days
    ) {

        LocalDate today =
                LocalDate.now();

        LocalDate startDate =
                today.minusDays(days);

        Branch branch = getUserBranch();

        Double totalSales =
                salesitemRepository
                        .getTotalSales(
                                startDate.atStartOfDay(),
                                branch
                        );

        Double totalExpenses =
                expenseRepository
                        .getTotalExpenses(
                                startDate,
                                branch
                        );

        if (totalSales == null) {
            totalSales = 0.0;
        }

        if (totalExpenses == null) {
            totalExpenses = 0.0;
        }

        Double netProfit =
                totalSales -
                totalExpenses;
System.out.println("Branch = " + branch.getBranchid());
System.out.println("Total Sales = " + totalSales);
System.out.println("Total Expenses = " + totalExpenses);
System.out.println("Total netProfit = " + netProfit);

        return new ProfitLossDto(
                totalSales,
                totalExpenses,
                netProfit
        );
    }
}