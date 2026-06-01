package com.example.Backend.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Dto.DashboardSummaryDto;
import com.example.Backend.Dto.ExpenseCategoryDto;
import com.example.Backend.Dto.ProfitLossDto;
import com.example.Backend.Dto.SalesChartDto;
import com.example.Backend.Dto.StockStatusDto;
import com.example.Backend.Dto.TopSellingProductDto;
import com.example.Backend.Service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto>
    getSummary() {

        return ResponseEntity.ok(
                dashboardService.getSummary()
        );
    }
    @GetMapping("/sales-chart")
public ResponseEntity<List<SalesChartDto>>
getSalesChart(
        @RequestParam int days
) {

    return ResponseEntity.ok(
            dashboardService
                    .getSalesChart(days)
    );
}
 @GetMapping("/top-products")
    public ResponseEntity<List<TopSellingProductDto>>
    getTopSellingProducts() {

        return ResponseEntity.ok(
                dashboardService
                        .getTopSellingProducts()
        );
    }
@GetMapping("/stock-status")
public ResponseEntity<
        List<StockStatusDto>
> getStockStatus() {

    return ResponseEntity.ok(
            dashboardService
                    .getStockStatus()
    );
}

@GetMapping("/expenses/7days")
public ResponseEntity<List<ExpenseCategoryDto>>
getLast7DaysExpenses() {

    return ResponseEntity.ok(
            dashboardService
                    .getLast7DaysExpenses()
    );
}

@GetMapping("/expenses/30days")
public ResponseEntity<List<ExpenseCategoryDto>>
getLast30DaysExpenseSplit() {

    return ResponseEntity.ok(
            dashboardService
                    .getLast30DaysExpenseSplit()
    );
}

@GetMapping("/profit-loss")
public ResponseEntity<ProfitLossDto>
getProfitLoss(
        @RequestParam(
                defaultValue = "30"
        )
        int days
) {

    return ResponseEntity.ok(
            dashboardService
                    .getProfitLoss(
                            days
                    )
    );
}




}