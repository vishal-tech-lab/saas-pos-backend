package com.example.Backend.Service;

import com.example.Backend.Dto.SalesItemDto;
import com.example.Backend.Dto.SalesReportDto;
import com.example.Backend.Dto.SalesReportItemDto;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.BranchStock;
import com.example.Backend.Entity.Product;
import com.example.Backend.Entity.RegisterSession;
import com.example.Backend.Entity.Salesitem;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.BranchStockRepository;
import com.example.Backend.Repository.ProductRepository;
import com.example.Backend.Repository.RegisterSessionRepository;
import com.example.Backend.Repository.SalesitemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class SalesitemService {

    private final SalesitemRepository salesitemRepository;
    private final ProductRepository productRepository;
    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;
    private final RegisterSessionRepository registerSessionRepository;

   public void closeRegister(Long branchId) {

    Branch branch =
            branchRepository.findById(branchId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Branch not found"
                            ));

    RegisterSession session =
            registerSessionRepository
                    .findByBranchAndActiveTrue(
                            branch
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No active session"
                            ));

    SalesReportDto report =
            getSalesReport(branchId);

    session.setTotalSales(
            report.getTotalSales()
    );

    session.setCashSales(
            report.getCashSales()
    );

    session.setUpiSales(
            report.getUpiSales()
    );

    session.setTotalBills(
            report.getTotalBills()
    );

    session.setClosedat(
            LocalDateTime.now()
    );

    session.setActive(false);

    registerSessionRepository.save(
            session
    );

    // AUTO OPEN NEW SESSION
 if (
    registerSessionRepository
        .findByBranchAndActiveTrue(branch)
        .isPresent()
) {
    return;
}
    RegisterSession newSession =
            new RegisterSession();

    newSession.setBranch(branch);

    newSession.setOpenedat(
            LocalDateTime.now()
    );

    newSession.setActive(true);

    newSession.setTotalSales(0.0);

    newSession.setCashSales(0.0);

    newSession.setUpiSales(0.0);

    newSession.setTotalBills(0);

    registerSessionRepository.save(
            newSession
    );
}
    @Transactional
    public Salesitem registerSalesitem(SalesItemDto salesItemDto) {
        if (salesItemDto == null) {
            throw new RuntimeException("Sales item request must not be null.");
        }
        if (salesItemDto.getBranchName() == null || salesItemDto.getBranchName().isBlank()) {
            throw new RuntimeException("Branch name must be specified.");
        }
        if (salesItemDto.getProductName() == null || salesItemDto.getProductName().isBlank()) {
            throw new RuntimeException("Product name must be specified.");
        }

        Branch branch = branchRepository.findByBranchname(salesItemDto.getBranchName())
                .orElseThrow(() -> new RuntimeException("Branch not found: " + salesItemDto.getBranchName()));

        Product product = productRepository.findByItemname(salesItemDto.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found: " + salesItemDto.getProductName()));

        Salesitem salesitem = new Salesitem();

salesitem.setBranch(branch);

salesitem.setItemname(
        product.getItemname()
);

salesitem.setQty(
        salesItemDto.getQty()
);

salesitem.setPaymentmethod(
        salesItemDto.getPaymentmethod()
);

salesitem.setBillno(
        salesItemDto.getBillno()
);

Double price =
        product.getPrice();

Double total =
        price *
        salesItemDto.getQty();

salesitem.setPrice(price);

salesitem.setTotal(total);

        return registerSalesitem(salesitem);
    }

    @Transactional
    public Salesitem registerSalesitem(Salesitem salesitem) {

        validateSalesitem(salesitem);

        Product product = productRepository.findByItemname(salesitem.getItemname())
                .orElseThrow(() -> new RuntimeException("Product not found for item: " + salesitem.getItemname()));

        BranchStock branchStock = branchStockRepository.findByBranchAndProduct(salesitem.getBranch(), product)
                .orElseThrow(() -> new RuntimeException("Insufficient stock"));

        if (salesitem.getQty() == null || salesitem.getQty() <= 0) {
            throw new RuntimeException("Insufficient stock");
        }

        if (branchStock.getQty() < salesitem.getQty()) {
            throw new RuntimeException("Insufficient stock");
        }

        branchStock.setQty(branchStock.getQty() - salesitem.getQty());
        if (branchStock.getQty() < 0) {
            throw new RuntimeException("Insufficient stock");
        }

        branchStockRepository.save(branchStock);

        salesitem.setCreatedat(LocalDateTime.now());
        return salesitemRepository.save(salesitem);
    }

    private void validateSalesitem(Salesitem salesitem) {
        if (salesitem == null) {
            throw new RuntimeException("Salesitem must not be null.");
        }
        if (salesitem.getBranch() == null) {
            throw new RuntimeException("Branch must be specified.");
        }
        if (salesitem.getItemname() == null || salesitem.getItemname().isBlank()) {
            throw new RuntimeException("Item name must be specified.");
        }
        if (salesitem.getQty() == null || salesitem.getQty() <= 0) {
            throw new RuntimeException("Quantity must be greater than zero.");
        }
    }

    public List<Salesitem> getAllSalesitems() {
        return salesitemRepository.findAll();
    }

    public List<Salesitem> getAllSalesitems(Long branchId) {
        if (branchId == null) {
            return getAllSalesitems();
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        return salesitemRepository.findByBranch(branch);
    }

    public List<Salesitem> getSalesitemsByBillno(String billno, Long branchId) {
        if (branchId == null) {
            return getSalesitemsByBillno(billno);
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        return salesitemRepository.findByBillnoAndBranch(billno, branch);
    }

    public SalesReportDto getSalesReport(Long branchId) {
Optional<RegisterSession> lastSession =
        registerSessionRepository
                .findTopByOrderByClosedatDesc();

LocalDateTime startTime =
        lastSession
                .map(RegisterSession::getClosedat)
                .orElse(LocalDate.now().atStartOfDay());
        List<Salesitem> salesitems;
        if (branchId == null) {
            salesitems = salesitemRepository.findByCreatedatBetween(startTime, LocalDateTime.now());
        } else {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            salesitems = salesitemRepository.findByBranchAndCreatedatBetween(branch, startTime, LocalDateTime.now());
        }

        return buildSalesReport(salesitems);
    }

    public SalesReportDto getSalesReport() {
        return getSalesReport(null);
    }

    public Optional<Salesitem> getSalesitemById(Long id) {

        return salesitemRepository.findById(id);
    }

    public List<Salesitem> getSalesitemsByBillno(String billno) {

        return salesitemRepository.findByBillno(billno);
    }

    public Salesitem updateSalesitem(Long id, Salesitem salesitemDetails) {

        Optional<Salesitem> optionalSalesitem = salesitemRepository.findById(id);

        if (optionalSalesitem.isPresent()) {

            Salesitem salesitem = optionalSalesitem.get();

            if (salesitemDetails.getBillno() != null) {
                salesitem.setBillno(salesitemDetails.getBillno());
            }

            if (salesitemDetails.getItemname() != null) {
                salesitem.setItemname(salesitemDetails.getItemname());
            }

            if (salesitemDetails.getQty() != null) {
                salesitem.setQty(salesitemDetails.getQty());
            }

            if (salesitemDetails.getPrice() != null) {
                salesitem.setPrice(salesitemDetails.getPrice());
            }

            if (salesitemDetails.getTotal() != null) {
                salesitem.setTotal(salesitemDetails.getTotal());
            }

            if (salesitemDetails.getCustomerid() != null) {
                salesitem.setCustomerid(salesitemDetails.getCustomerid());
            }

            if (salesitemDetails.getPaymentmethod() != null) {
                salesitem.setPaymentmethod(salesitemDetails.getPaymentmethod());
            }

            return salesitemRepository.save(salesitem);
        }

        return null;
    }

    public boolean deleteSalesitem(Long id) {

        if (salesitemRepository.existsById(id)) {

            salesitemRepository.deleteById(id);

            return true;
        }

        return false;
    }

    private SalesReportDto buildSalesReport(List<Salesitem> salesitems) {
        Double totalSales = 0.0;
        Double cashSales = 0.0;
        Double upiSales = 0.0;

        Set<String> billnos = new HashSet<>();
        List<SalesReportItemDto> reportItems = new ArrayList<>();

        for (Salesitem item : salesitems) {
            totalSales += item.getTotal();
            billnos.add(item.getBillno());

            if (item.getPaymentmethod() != null) {
                if (item.getPaymentmethod().equalsIgnoreCase("Cash")) {
                    cashSales += item.getTotal();
                }
                if (item.getPaymentmethod().equalsIgnoreCase("UPI")) {
                    upiSales += item.getTotal();
                }
            }

            String category = "OTHERS";
            Optional<Product> product = productRepository.findByItemname(item.getItemname());
            if (product.isPresent()) {
                category = product.get().getCategory();
            }

            boolean itemExists = false;
            for (SalesReportItemDto dto : reportItems) {
                if (dto.getItemname().equalsIgnoreCase(item.getItemname())) {
                    dto.setQty(dto.getQty() + item.getQty());
                    dto.setTotal(dto.getTotal() + item.getTotal());
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                reportItems.add(
                        new SalesReportItemDto(
                                item.getItemname(),
                                category,
                                item.getQty(),
                                item.getTotal()
                        )
                );
            }
        }

        return new SalesReportDto(
                totalSales,
                billnos.size(),
                cashSales,
                upiSales,
                reportItems
        );
    }
}
