package com.example.Backend.Controller;

import com.example.Backend.Dto.CustomerOrderResponseDto;
import com.example.Backend.Service.CustomerOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/kitchen")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class KitchenController {

    private final CustomerOrderService customerOrderService;

    /**
     * Get all pending and confirmed orders for kitchen
     * Protected API (KITCHEN role) - GET /kitchen/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<List<CustomerOrderResponseDto>> getKitchenOrders() {
        List<CustomerOrderResponseDto> orders = customerOrderService.getKitchenOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Update order status by kitchen staff
     * Protected API (KITCHEN role) - PUT /kitchen/order/{orderId}/status
     */
    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<CustomerOrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        CustomerOrderResponseDto updatedOrder = customerOrderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
}
