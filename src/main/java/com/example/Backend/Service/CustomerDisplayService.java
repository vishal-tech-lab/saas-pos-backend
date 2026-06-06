package com.example.Backend.Service;

import com.example.Backend.Dto.CustomerDisplayDto;
import com.example.Backend.Dto.CustomerDisplayItemDto;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.CustomerDisplay;
import com.example.Backend.Entity.CustomerDisplayItem;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.CustomerDisplayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CustomerDisplayService
 * Service for managing customer display operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerDisplayService {

    private final CustomerDisplayRepository customerDisplayRepository;
    private final BranchRepository branchRepository;
    private final CustomerDisplayWebSocketService customerDisplayWebSocketService;

    /**
     * Update or create customer display
     * If display exists for branch: Update it
     * Else: Create new display
     * Replace all old display items with new items
     *
     * @param dto CustomerDisplayDto containing display data
     * @return Updated CustomerDisplayDto
     */
    public CustomerDisplayDto updateDisplay(CustomerDisplayDto dto) {
        log.info("Updating customer display for branch: {}", dto.getBranchid());

        // Validate branch exists
        Branch branch = branchRepository.findById(dto.getBranchid())
                .orElseThrow(() -> {
                    log.error("Branch not found with id: {}", dto.getBranchid());
                    return new IllegalArgumentException("Branch not found with id: " + dto.getBranchid());
                });

        // Find or create display
        CustomerDisplay display = customerDisplayRepository
                .findByBranch_Branchid(dto.getBranchid())
                .orElseGet(() -> {
                    log.info("Creating new customer display for branch: {}", dto.getBranchid());
                    CustomerDisplay newDisplay = new CustomerDisplay();
                    newDisplay.setBranch(branch);
                    newDisplay.setItems(new ArrayList<>());
                    return newDisplay;
                });

        // Update display properties
        display.setBillno(dto.getBillno());
        display.setTotal(dto.getTotal() != null ? dto.getTotal() : 0.0);
        display.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        display.setUpdatedat(LocalDateTime.now());

        // Clear existing items and add new ones
        display.getItems().clear();

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<CustomerDisplayItem> newItems = dto.getItems().stream()
                    .map(itemDto -> {
                        CustomerDisplayItem item = new CustomerDisplayItem();
                        item.setDisplay(display);
                        item.setItemname(itemDto.getItemname());
                        item.setQty(itemDto.getQty());
                        item.setPrice(itemDto.getPrice());
                        item.setTotal(itemDto.getTotal());
                        return item;
                    })
                    .collect(Collectors.toList());

            display.getItems().addAll(newItems);
        }

        // Save and return
        CustomerDisplay savedDisplay = customerDisplayRepository.save(display);
        CustomerDisplayDto responseDto = mapToDto(savedDisplay);
        customerDisplayWebSocketService.broadcastDisplay(responseDto);
        log.info("Customer display updated successfully for branch: {}", dto.getBranchid());

        return responseDto;
    }

    /**
     * Get current customer display for a branch
     *
     * @param branchid the branch id
     * @return CustomerDisplayDto of current display
     */
    @Transactional(readOnly = true)
    public CustomerDisplayDto getCurrentDisplay(Long branchid) {
        log.info("Fetching current customer display for branch: {}", branchid);

        // Validate branch exists
        if (!branchRepository.existsById(branchid)) {
            log.error("Branch not found with id: {}", branchid);
            throw new IllegalArgumentException("Branch not found with id: " + branchid);
        }

        Optional<CustomerDisplay> displayOpt =
            customerDisplayRepository
                .findByBranch_Branchid(branchid);

        if (displayOpt.isEmpty()) {

            log.info(
                "No display found for branch {}. Returning empty display.",
                branchid
            );

            CustomerDisplayDto dto =
                new CustomerDisplayDto();

            dto.setBranchid(branchid);
            dto.setBillno(null);
            dto.setTotal(0.0);
            dto.setStatus("CLEARED");
            dto.setItems(new ArrayList<>());

            return dto;
        }

        log.info("Current display fetched for branch: {}", branchid);
        return mapToDto(displayOpt.get());
    }

    /**
     * Clear customer display after bill completion
     * Remove all display items and reset display properties
     *
     * @param branchid the branch id
     * @return CustomerDisplayDto with cleared data
     */
    public CustomerDisplayDto clearDisplay(Long branchid) {
        log.info("Clearing customer display for branch: {}", branchid);

        // Validate branch exists
        if (!branchRepository.existsById(branchid)) {
            log.error("Branch not found with id: {}", branchid);
            throw new IllegalArgumentException("Branch not found with id: " + branchid);
        }

        CustomerDisplay display = customerDisplayRepository
                .findByBranch_Branchid(branchid)
                .orElseThrow(() -> {
                    log.warn("No display found for branch: {}", branchid);
                    return new IllegalArgumentException("No display found for branch id: " + branchid);
                });

        // Clear items
        display.getItems().clear();

        // Reset properties
        display.setBillno(null);
        display.setTotal(0.0);
        display.setStatus("CLEARED");
        display.setUpdatedat(LocalDateTime.now());

        CustomerDisplay savedDisplay = customerDisplayRepository.save(display);
        CustomerDisplayDto responseDto = mapToDto(savedDisplay);
        customerDisplayWebSocketService.broadcastDisplay(responseDto);

        log.info("Customer display cleared successfully for branch: {}", branchid);

        return mapToDto(savedDisplay);
    }

    /**
     * Map CustomerDisplay entity to CustomerDisplayDto
     *
     * @param display the CustomerDisplay entity
     * @return mapped CustomerDisplayDto
     */
    private CustomerDisplayDto mapToDto(CustomerDisplay display) {
        CustomerDisplayDto dto = new CustomerDisplayDto();
        dto.setBranchid(display.getBranch().getBranchid());
        dto.setBillno(display.getBillno());
        dto.setTotal(display.getTotal());
        dto.setStatus(display.getStatus());

        if (display.getItems() != null && !display.getItems().isEmpty()) {
            dto.setItems(display.getItems().stream()
                    .map(item -> new CustomerDisplayItemDto(
                            item.getItemname(),
                            item.getQty(),
                            item.getPrice(),
                            item.getTotal()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }
}
