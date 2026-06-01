package com.example.Backend.Service;

import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Repository.BranchStockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BranchStockService {

    private final BranchStockRepository branchStockRepository;

    public BranchStock registerBranchStock(BranchStock branchStock) {
        return branchStockRepository.save(branchStock);
    }

    public List<BranchStock> getAllBranchStocks() {
        return branchStockRepository.findAll();
    }

    public Optional<BranchStock> getBranchStockById(Long id) {
        return branchStockRepository.findById(id);
    }

    public Optional<BranchStock> updateBranchStock(Long id, BranchStock branchStock) {
        return branchStockRepository.findById(id).map(existing -> {
            if (branchStock.getBranch() != null) {
                existing.setBranch(branchStock.getBranch());
            }
            if (branchStock.getProduct() != null) {
                existing.setProduct(branchStock.getProduct());
            }
            if (branchStock.getQty() != null) {
                existing.setQty(branchStock.getQty());
            }
            return branchStockRepository.save(existing);
        });
    }

    public boolean deleteBranchStock(Long id) {
        if (branchStockRepository.existsById(id)) {
            branchStockRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
