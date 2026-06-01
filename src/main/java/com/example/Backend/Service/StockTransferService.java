package com.example.Backend.Service;

import com.example.Backend.Dto.StockTransferDto;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Entity.Product;
import com.example.Backend.Entity.StockTransfer;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.BranchStockRepository;
import com.example.Backend.Repository.ProductRepository;
import com.example.Backend.Repository.StockTransferRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Transactional
    public StockTransfer registerStockTransfer(StockTransferDto transferDto) {
        if (transferDto == null) {
            throw new RuntimeException("Stock transfer request must not be null.");
        }

        Branch fromBranch = branchRepository.findByBranchname(transferDto.getFrombranch())
                .orElseThrow(() -> new RuntimeException("Source branch not found: " + transferDto.getFrombranch()));

        Branch toBranch = branchRepository.findByBranchname(transferDto.getTobranch())
                .orElseThrow(() -> new RuntimeException("Destination branch not found: " + transferDto.getTobranch()));

        Product product = productRepository.findByItemname(transferDto.getProductname())
                .orElseThrow(() -> new RuntimeException("Product not found: " + transferDto.getProductname()));

        StockTransfer transfer = new StockTransfer();
        transfer.setFromBranch(fromBranch);
        transfer.setToBranch(toBranch);
        transfer.setProduct(product);
        transfer.setQty(transferDto.getQty());

        return transferStock(transfer);
    }

    @Transactional
    public StockTransfer transferStock(StockTransfer transfer) {
        validateTransfer(transfer);

        BranchStock fromStock = branchStockRepository
                .findByBranchAndProduct(transfer.getFromBranch(), transfer.getProduct())
                .orElseThrow(() -> new RuntimeException("Insufficient stock in source branch."));

        double transferQty = transfer.getQty();
        if (fromStock.getQty() < transferQty) {
            throw new RuntimeException("Source branch stock is insufficient for transfer.");
        }

        fromStock.setQty(fromStock.getQty() - transferQty);
        branchStockRepository.save(fromStock);

        BranchStock toStock = branchStockRepository
                .findByBranchAndProduct(transfer.getToBranch(), transfer.getProduct())
                .orElseGet(() -> createBranchStock(transfer.getToBranch(), transfer.getProduct(), 0.0));

        toStock.setQty(toStock.getQty() + transferQty);
        branchStockRepository.save(toStock);

        if (transfer.getTransferdate() == null) {
            transfer.setTransferdate(LocalDateTime.now());
        }

        return stockTransferRepository.save(transfer);
    }

    public StockTransfer registerStockTransfer(StockTransfer transfer) {
        return transferStock(transfer);
    }

    public List<StockTransfer> getAllStockTransfers() {
        return stockTransferRepository.findAll();
    }

    public Optional<StockTransfer> getStockTransferById(Long id) {
        return stockTransferRepository.findById(id);
    }

    public Optional<StockTransfer> updateStockTransfer(Long id, StockTransfer stockTransfer) {
        return stockTransferRepository.findById(id).map(existing -> {
            if (stockTransfer.getFromBranch() != null) {
                existing.setFromBranch(stockTransfer.getFromBranch());
            }
            if (stockTransfer.getToBranch() != null) {
                existing.setToBranch(stockTransfer.getToBranch());
            }
            if (stockTransfer.getProduct() != null) {
                existing.setProduct(stockTransfer.getProduct());
            }
            if (stockTransfer.getQty() != null) {
                existing.setQty(stockTransfer.getQty());
            }
            if (stockTransfer.getTransferdate() != null) {
                existing.setTransferdate(stockTransfer.getTransferdate());
            }
            return stockTransferRepository.save(existing);
        });
    }

    public boolean deleteStockTransfer(Long id) {
        if (stockTransferRepository.existsById(id)) {
            stockTransferRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateTransfer(StockTransfer transfer) {
        if (transfer == null) {
            throw new RuntimeException("Stock transfer data must not be null.");
        }
        if (transfer.getFromBranch() == null) {
            throw new RuntimeException("Source branch must be specified.");
        }
        if (transfer.getToBranch() == null) {
            throw new RuntimeException("Destination branch must be specified.");
        }
        if (transfer.getProduct() == null) {
            throw new RuntimeException("Product must be specified for stock transfer.");
        }
        if (transfer.getQty() == null || transfer.getQty() <= 0) {
            throw new RuntimeException("Transfer quantity must be greater than zero.");
        }
    }

    private BranchStock createBranchStock(Branch branch, Product product, Double initialQty) {
        BranchStock stock = new BranchStock();
        stock.setBranch(branch);
        stock.setProduct(product);
        stock.setQty(initialQty);
        return stock;
    }
}
