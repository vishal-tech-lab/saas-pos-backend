package com.example.Backend.Controller;

import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Service.BranchStockService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branch-stock")
@AllArgsConstructor
public class BranchStockController {

    private final BranchStockService branchStockService;

    @PostMapping("/register")
    public ResponseEntity<BranchStock> registerBranchStock(@RequestBody BranchStock branchStock) {
        BranchStock created = branchStockService.registerBranchStock(branchStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BranchStock>> getAllBranchStocks() {
        return ResponseEntity.ok(branchStockService.getAllBranchStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchStock> getBranchStockById(@PathVariable Long id) {
        return branchStockService.getBranchStockById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchStock> updateBranchStock(@PathVariable Long id, @RequestBody BranchStock branchStock) {
        return branchStockService.updateBranchStock(id, branchStock)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranchStock(@PathVariable Long id) {
        if (branchStockService.deleteBranchStock(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
