package com.example.Backend.Service;

import com.example.Backend.Dto.CreateCustomerOrderDto;
import com.example.Backend.Dto.CustomerOrderItemDto;
import com.example.Backend.Dto.CustomerOrderResponseDto;
import com.example.Backend.Dto.CustomerOrderItemResponseDto;
import com.example.Backend.Entity.CustomerOrder;
import com.example.Backend.Entity.CustomerOrderItem;
import com.example.Backend.Entity.Product;
import com.example.Backend.Entity.TableMaster;
import com.example.Backend.Exception.InvalidOrderException;
import com.example.Backend.Exception.InvalidTableException;
import com.example.Backend.Exception.ResourceNotFoundException;
import com.example.Backend.Repository.CustomerOrderRepository;
import com.example.Backend.Repository.CustomerOrderItemRepository;
import com.example.Backend.Repository.TableMasterRepository;
import com.example.Backend.Repository.ProductRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemRepository customerOrderItemRepository;
    private final TableMasterRepository tableMasterRepository;
    private final ProductRepository productRepository;

    /**
     * Create a new customer order
     * Validates table and products, calculates totals, saves order and items
     */
    public CustomerOrderResponseDto createOrder(CreateCustomerOrderDto orderDto) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidOrderException("Tenant context not set");
        }

        // Validate table
        Optional<TableMaster> tableOptional = tableMasterRepository.findById(orderDto.getTableId());
        if (tableOptional.isEmpty()) {
            throw new InvalidTableException("Table not found with id: " + orderDto.getTableId());
        }
TableMaster table = tableOptional.get();

CustomerOrder order = new CustomerOrder();

order.setTable(table);
order.setBranchid(table.getBranchid());

order.setPaymentStatus("PENDING");
order.setOrderStatus("PENDING");
order.setCreatedAt(LocalDateTime.now());
order.setUpdatedAt(LocalDateTime.now());

        // Calculate total and create order items
        Double totalAmount = 0.0;
        List<CustomerOrderItem> items = new java.util.ArrayList<>();

        for (CustomerOrderItemDto itemDto : orderDto.getItems()) {
            // Validate product
            Optional<Product> productOptional = productRepository.findById(itemDto.getProductId());
            if (productOptional.isEmpty()) {
                throw new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId());
            }

            Product product = productOptional.get();
            Double itemTotal = product.getPrice() * itemDto.getQty();

            // Create order item
            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getItemname());
            orderItem.setQty(itemDto.getQty());
            orderItem.setPrice(product.getPrice());
            orderItem.setTotal(itemTotal);

            items.add(orderItem);
            totalAmount += itemTotal;
        }

        order.setTotalAmount(totalAmount);
        order.setItems(items);

        // Save order and items
        CustomerOrder savedOrder = customerOrderRepository.save(order);

        // Return response DTO
        return convertToResponseDto(savedOrder);
    }

    /**
     * Get order by ID
     */
    public CustomerOrderResponseDto getOrderById(Long orderId) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidOrderException("Tenant context not set");
        }

        Optional<CustomerOrder> orderOptional = customerOrderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return convertToResponseDto(orderOptional.get());
    }

    /**
     * Get all kitchen orders (PENDING and CONFIRMED orders)
     */
    public List<CustomerOrderResponseDto> getKitchenOrders() {
        if (TenantContext.getTenant() == null) {
            throw new InvalidOrderException("Tenant context not set");
        }

        List<CustomerOrder> kitchenOrders = customerOrderRepository.findAll();

        return kitchenOrders.stream()
                .filter(order ->
                        !order.getOrderStatus().equals("SERVED") &&
                        !order.getOrderStatus().equals("CANCELLED"))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    public CustomerOrderResponseDto updateOrderStatus(Long orderId, String orderStatus) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidOrderException("Tenant context not set");
        }

        Optional<CustomerOrder> orderOptional = customerOrderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        CustomerOrder order = orderOptional.get();

        // Validate status
        if (!isValidOrderStatus(orderStatus)) {
            throw new InvalidOrderException("Invalid order status: " + orderStatus);
        }

        order.setOrderStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());

        CustomerOrder updatedOrder = customerOrderRepository.save(order);
        return convertToResponseDto(updatedOrder);
    }

    /**
     * Get customer order status
     */
    public CustomerOrderResponseDto getCustomerOrderStatus(Long orderId) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidOrderException("Tenant context not set");
        }

        Optional<CustomerOrder> orderOptional = customerOrderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return convertToResponseDto(orderOptional.get());
    }

    /**
     * Convert entity to response DTO
     */
    private CustomerOrderResponseDto convertToResponseDto(CustomerOrder order) {
        List<CustomerOrderItemResponseDto> itemsDto = order.getItems().stream()
                .map(item -> new CustomerOrderItemResponseDto(
                        item.getId(),
                        item.getProductName(),
                        item.getQty(),
                        item.getPrice(),
                        item.getTotal()
                ))
                .collect(Collectors.toList());

        return new CustomerOrderResponseDto(
                order.getOrderId(),
                order.getTable().getTableName(),
                order.getTotalAmount(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                itemsDto
        );
    }

    /**
     * Validate order status
     */
    private boolean isValidOrderStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("CONFIRMED") || 
               status.equals("PREPARING") || 
               status.equals("READY") || 
               status.equals("SERVED") || 
               status.equals("CANCELLED");
    }
}
