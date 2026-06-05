package com.example.Backend.Controller;

import com.example.Backend.Dto.ApiResponse;
import com.example.Backend.Dto.CustomerDisplayDto;
import com.example.Backend.Service.CustomerDisplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CustomerDisplayController
 * REST API controller for customer display operations
 * Base URL: /customer-display
 */
@RestController
@RequestMapping("/customer-display")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerDisplayController {

    private final CustomerDisplayService customerDisplayService;
    /**
     * Update or create customer display
     * Endpoint: POST /customer-display/update
     *
     * @param dto CustomerDisplayDto containing display data
     * @return ResponseEntity with updated display data
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<CustomerDisplayDto>> updateDisplay(
            @RequestBody CustomerDisplayDto dto) {
        try {
            log.info("Received request to update customer display for branch: {}", dto.getBranchid());

            // Validate input
            if (dto.getBranchid() == null || dto.getBranchid() <= 0) {
                log.error("Invalid branch id: {}", dto.getBranchid());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Invalid branch id", null));
            }

            CustomerDisplayDto updatedDisplay = customerDisplayService.updateDisplay(dto);

            log.info("Customer display updated successfully for branch: {}", dto.getBranchid());
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Display updated successfully", updatedDisplay));

        } catch (IllegalArgumentException e) {
            log.error("Validation error while updating display: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating customer display: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error updating display: " + e.getMessage(), null));
        }
    }

    /**
     * Get current customer display for a branch
     * Endpoint: GET /customer-display/current/{branchid}
     *
     * @param branchid the branch id
     * @return ResponseEntity with current display data
     */
    @GetMapping("/current/{branchid}")
    public ResponseEntity<ApiResponse<CustomerDisplayDto>> getCurrentDisplay(
            @PathVariable Long branchid) {
        try {
            log.info("Received request to fetch current display for branch: {}", branchid);

            // Validate input
            if (branchid == null || branchid <= 0) {
                log.error("Invalid branch id: {}", branchid);
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Invalid branch id", null));
            }

            CustomerDisplayDto currentDisplay = customerDisplayService.getCurrentDisplay(branchid);

            log.info("Current display fetched successfully for branch: {}", branchid);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(
                            "Display fetched successfully",
                            currentDisplay
                    ));

        } catch (IllegalArgumentException e) {
            log.error("Validation error while fetching display: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error fetching current display: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Error fetching display: " + e.getMessage()
                    ));
        }
    }

    /**
     * Clear customer display after bill completion
     * Endpoint: DELETE /customer-display/clear/{branchid}
     *
     * @param branchid the branch id
     * @return ResponseEntity with cleared display data
     */
    @DeleteMapping("/clear/{branchid}")
    public ResponseEntity<ApiResponse<CustomerDisplayDto>> clearDisplay(
            @PathVariable Long branchid) {
        try {
            log.info("Received request to clear display for branch: {}", branchid);

            // Validate input
            if (branchid == null || branchid <= 0) {
                log.error("Invalid branch id: {}", branchid);
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Invalid branch id", null));
            }

            CustomerDisplayDto clearedDisplay = customerDisplayService.clearDisplay(branchid);

            log.info("Customer display cleared successfully for branch: {}", branchid);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Display cleared successfully", clearedDisplay));

        } catch (IllegalArgumentException e) {
            log.error("Validation error while clearing display: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error clearing display: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error clearing display: " + e.getMessage(), null));
        }
    }
}
