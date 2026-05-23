package com.example.Backend.Service;

import com.example.Backend.Dto.SalesReportDto;
import com.example.Backend.Dto.SalesReportItemDto;
import com.example.Backend.Entity.Product;
import com.example.Backend.Entity.Salesitem;
import com.example.Backend.Repository.CloseregisterRepository;
import com.example.Backend.Repository.ProductRepository;
import com.example.Backend.Repository.SalesitemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Backend.Entity.Closeregister;
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
    private final CloseregisterRepository closeregisterRepository;
    public void closeRegister() {

    Closeregister closeregister =
            new Closeregister();

    closeregister.setClosedat(
            LocalDateTime.now()
    );

    closeregisterRepository.save(
            closeregister
    );
}
    public Salesitem registerSalesitem(Salesitem salesitem) {

        salesitem.setCreatedat(LocalDateTime.now());

        return salesitemRepository.save(salesitem);
    }

    public List<Salesitem> getAllSalesitems() {

        return salesitemRepository.findAll();
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

    public SalesReportDto getSalesReport() {

        Closeregister lastClose =
                closeregisterRepository
                        .findTopByOrderByClosedatDesc();

        LocalDateTime startTime;

        if (lastClose != null) {

            startTime =
                    lastClose.getClosedat();

        } else {

            startTime =
                    LocalDate.now()
                            .atStartOfDay();
        }

        List<Salesitem> salesitems =
                salesitemRepository
                        .findByCreatedatBetween(
                                startTime,
                                LocalDateTime.now()
                        );

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