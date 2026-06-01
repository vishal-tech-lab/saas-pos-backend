package com.example.Backend.Controller;

import com.example.Backend.Dto.SalesItemDto;
import com.example.Backend.Dto.SalesReportDto;
import com.example.Backend.Entity.Salesitem;
import com.example.Backend.Service.SalesitemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/salesitem")
@AllArgsConstructor

public class SalesitemController {

    private final SalesitemService salesitemService;

    /**
     * Register a new sales item
     * POST /salesitem/register
     */
    @PostMapping("/register")
    public ResponseEntity<Salesitem> registerSalesitem(@RequestBody SalesItemDto salesItemDto) {
        Salesitem savedSalesitem = salesitemService.registerSalesitem(salesItemDto);
        return new ResponseEntity<>(savedSalesitem, HttpStatus.CREATED);
    }
@PostMapping("/closeregister/{branchId}")
public ResponseEntity<String> closeRegister(
        @PathVariable Long branchId
) {
    salesitemService.closeRegister(branchId);

    return ResponseEntity.ok(
            "Register Closed"
    );
}  
    @GetMapping("/all")
    public ResponseEntity<List<Salesitem>> getAllSalesitems(@RequestParam(required = false) Long branchid) {
        List<Salesitem> salesitems = salesitemService.getAllSalesitems(branchid);
        return new ResponseEntity<>(salesitems, HttpStatus.OK);
    }

    /**
     * Get sales items by bill number
     * GET /salesitem/bill/{billno}
     */
    @GetMapping("/bill/{billno}")
    public ResponseEntity<List<Salesitem>> getSalesitemsByBillno(
            @PathVariable String billno,
            @RequestParam(required = false) Long branchid
    ) {
        List<Salesitem> salesitems = salesitemService.getSalesitemsByBillno(billno, branchid);
        if (!salesitems.isEmpty()) {
            return new ResponseEntity<>(salesitems, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Get sales item by ID
     * GET /salesitem/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salesitem> getSalesitemById(@PathVariable Long id) {
        Optional<Salesitem> salesitem = salesitemService.getSalesitemById(id);
        return salesitem.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Update sales item
     * PUT /salesitem/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Salesitem> updateSalesitem(@PathVariable Long id, @RequestBody Salesitem salesitemDetails) {
        Salesitem updatedSalesitem = salesitemService.updateSalesitem(id, salesitemDetails);
        if (updatedSalesitem != null) {
            return new ResponseEntity<>(updatedSalesitem, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Delete sales item
     * DELETE /salesitem/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSalesitem(@PathVariable Long id) {
        boolean deleted = salesitemService.deleteSalesitem(id);
        if (deleted) {
            return new ResponseEntity<>("Salesitem deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Salesitem not found", HttpStatus.NOT_FOUND);
    }

    /**
 * Sales report
 * GET /salesitem/report
 */
@GetMapping("/report")
public ResponseEntity<SalesReportDto> getSalesReport(@RequestParam(required = false) Long branchid) {

    SalesReportDto report =
            salesitemService.getSalesReport(branchid);

    return new ResponseEntity<>(
            report,
            HttpStatus.OK
    );
}
}
