package com.example.Backend.Controller;

import com.example.Backend.Dto.CreateCustomerOrderDto;
import com.example.Backend.Dto.CustomerOrderResponseDto;
import com.example.Backend.Entity.Product;
import com.example.Backend.Service.CustomerOrderService;
import com.example.Backend.Service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final ProductService productService;

    /**
     * Get all available products for customer menu
     * Public API - GET /customer-menu/products
     */
    @GetMapping("/customer-menu/products")
    public ResponseEntity<List<Product>> getMenuProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Create a new customer order
     * Public API - POST /customer-order/create
     */
    @PostMapping("/customer-order/create")
    public ResponseEntity<CustomerOrderResponseDto> createOrder(@RequestBody CreateCustomerOrderDto orderDto) {
        CustomerOrderResponseDto createdOrder = customerOrderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * Get customer order status by order ID
     * Public API - GET /customer-order/status/{orderId}
     */
    @GetMapping("/customer-order/status/{orderId}")
    public ResponseEntity<CustomerOrderResponseDto> getOrderStatus(@PathVariable Long orderId) {
        CustomerOrderResponseDto order = customerOrderService.getCustomerOrderStatus(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
