package com.example.Backend.Controller;

import com.example.Backend.Dto.StockTransferDto;
import com.example.Backend.Entity.StockTransfer;
import com.example.Backend.Service.StockTransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocktransfer")
@AllArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;

    @PostMapping("/register")
    public ResponseEntity<StockTransfer> registerStockTransfer(@RequestBody StockTransferDto stockTransferDto) {
        StockTransfer created = stockTransferService.registerStockTransfer(stockTransferDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<StockTransfer>> getAllStockTransfers() {
        return ResponseEntity.ok(stockTransferService.getAllStockTransfers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockTransfer> getStockTransferById(@PathVariable Long id) {
        return stockTransferService.getStockTransferById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockTransfer> updateStockTransfer(@PathVariable Long id, @RequestBody StockTransfer stockTransfer) {
        return stockTransferService.updateStockTransfer(id, stockTransfer)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockTransfer(@PathVariable Long id) {
        if (stockTransferService.deleteStockTransfer(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
