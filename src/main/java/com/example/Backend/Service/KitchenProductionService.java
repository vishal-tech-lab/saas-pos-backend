package com.example.Backend.Service;

import com.example.Backend.Dto.KitchenProductionDto;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Entity.KitchenProduction;
import com.example.Backend.Entity.Product;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.BranchStockRepository;
import com.example.Backend.Repository.KitchenProductionRepository;
import com.example.Backend.Repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class KitchenProductionService {

    private final KitchenProductionRepository kitchenProductionRepository;
    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Transactional
    public KitchenProduction registerKitchenProduction(KitchenProductionDto productionDto) {
        if (productionDto == null) {
            throw new RuntimeException("Kitchen production request must not be null.");
        }

        Branch branch = branchRepository.findByBranchname(productionDto.getBranchname())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + productionDto.getBranchname()));

        Product product = productRepository.findByItemname(productionDto.getProductname())
                .orElseThrow(() -> new RuntimeException("Product not found: " + productionDto.getProductname()));

        KitchenProduction production = new KitchenProduction();
        production.setBranch(branch);
        production.setProduct(product);
        production.setQty(productionDto.getQty());
        production.setNotes(productionDto.getNotes());

        return registerKitchenProduction(production);
    }

    @Transactional
    public KitchenProduction registerKitchenProduction(KitchenProduction production) {
        validateProduction(production);

        Branch branch = production.getBranch();
        if (branch == null) {
            throw new RuntimeException("Production branch must be specified.");
        }

        if (!"CENTRAL_KITCHEN".equalsIgnoreCase(branch.getBranchtype())) {
            throw new RuntimeException("Only CENTRAL_KITCHEN branches can produce stock.");
        }

        Product product = production.getProduct();
        if (product == null) {
            throw new RuntimeException("Production product must be specified.");
        }

        BranchStock branchStock = branchStockRepository.findByBranchAndProduct(branch, product)
                .orElseGet(() -> createBranchStock(branch, product, 0.0));

        branchStock.setQty(branchStock.getQty() + production.getQty());
        branchStockRepository.save(branchStock);

        if (production.getProductiondate() == null) {
            production.setProductiondate(LocalDateTime.now());
        }

        return kitchenProductionRepository.save(production);
    }

    public List<KitchenProduction> getAllKitchenProductions() {
        return kitchenProductionRepository.findAll();
    }

    public Optional<KitchenProduction> getKitchenProductionById(Long id) {
        return kitchenProductionRepository.findById(id);
    }

    public boolean deleteKitchenProduction(Long id) {
        if (kitchenProductionRepository.existsById(id)) {
            kitchenProductionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateProduction(KitchenProduction production) {
        if (production == null) {
            throw new RuntimeException("Kitchen production must not be null.");
        }
        if (production.getBranch() == null) {
            throw new RuntimeException("Branch must be specified for kitchen production.");
        }
        if (production.getProduct() == null) {
            throw new RuntimeException("Product must be specified for kitchen production.");
        }
        if (production.getQty() == null || production.getQty() <= 0) {
            throw new RuntimeException("Production quantity must be greater than zero.");
        }
    }

    private BranchStock createBranchStock(Branch branch, Product product, Double initialQty) {
        BranchStock branchStock = new BranchStock();
        branchStock.setBranch(branch);
        branchStock.setProduct(product);
        branchStock.setQty(initialQty);
        return branchStock;
    }
}
