# Restaurant Ordering System - Backend Implementation

## Overview
This document provides comprehensive details about the new Restaurant Ordering System modules added to the SaaS backend. The system supports multi-tenant table management and customer order processing with kitchen production tracking.

## Architecture Overview

### Multi-Tenant Support
All APIs and services support multi-tenancy through the `X-Tenant-ID` header. The `TenantContext` is used to manage tenant isolation across the application.

## Entity Layer

### 1. **TableMaster**
Manages physical tables in the restaurant

**File:** `Entity/TableMaster.java`

**Fields:**
- `tableId` (Long): Primary key, auto-generated
- `tableName` (String): Unique name for the table
- `qrUrl` (String): URL for QR code linking to the customer menu
- `status` (String): Table status (ACTIVE, INACTIVE, RESERVED)
- `tenantId` (String): Multi-tenant identifier
- `orders` (List<CustomerOrder>): One-to-many relationship

**Key Features:**
- Cascade delete for related orders
- Lazy loading for orders
- Tenant isolation enforced

### 2. **CustomerOrder**
Represents a customer order placed at a table

**File:** `Entity/CustomerOrder.java`

**Fields:**
- `orderId` (Long): Primary key, auto-generated
- `table` (TableMaster): Foreign key reference
- `totalAmount` (Double): Total order amount
- `paymentStatus` (String): PENDING, COMPLETED, CANCELLED
- `orderStatus` (String): PENDING, CONFIRMED, PREPARING, READY, SERVED, CANCELLED
- `createdAt` (LocalDateTime): Order creation timestamp
- `updatedAt` (LocalDateTime): Last update timestamp
- `tenantId` (String): Multi-tenant identifier
- `items` (List<CustomerOrderItem>): One-to-many relationship

**Key Features:**
- Cascade delete for related items
- Lazy loading for items
- Automatic timestamps

### 3. **CustomerOrderItem**
Individual items in a customer order

**File:** `Entity/CustomerOrderItem.java`

**Fields:**
- `id` (Long): Primary key, auto-generated
- `order` (CustomerOrder): Foreign key reference
- `product` (Product): Foreign key reference
- `productName` (String): Snapshot of product name at order time
- `qty` (Double): Quantity ordered
- `price` (Double): Unit price at order time
- `total` (Double): Line item total (qty * price)
- `tenantId` (String): Multi-tenant identifier

## Repository Layer

### 1. **TableMasterRepository**
`Repository/TableMasterRepository.java`

**Methods:**
```java
List<TableMaster> findByStatus(String status);
Optional<TableMaster> findByTableName(String tableName);
List<TableMaster> findByTenantId(String tenantId);
```

### 2. **CustomerOrderRepository**
`Repository/CustomerOrderRepository.java`

**Methods:**
```java
List<CustomerOrder> findByOrderStatus(String orderStatus);
List<CustomerOrder> findByTable(TableMaster table);
List<CustomerOrder> findByTableAndOrderStatus(TableMaster table, String orderStatus);
List<CustomerOrder> findByTenantId(String tenantId);
Optional<CustomerOrder> findByOrderIdAndTenantId(Long orderId, String tenantId);
```

### 3. **CustomerOrderItemRepository**
`Repository/CustomerOrderItemRepository.java`

**Methods:**
```java
List<CustomerOrderItem> findByOrder(CustomerOrder order);
List<CustomerOrderItem> findByTenantId(String tenantId);
```

## DTO Layer

### 1. **CreateCustomerOrderDto**
`Dto/CreateCustomerOrderDto.java`

**Purpose:** Input DTO for creating orders

**Fields:**
```java
Long tableId;                           // Target table
List<CustomerOrderItemDto> items;       // Order items
```

### 2. **CustomerOrderItemDto**
`Dto/CustomerOrderItemDto.java`

**Purpose:** Input DTO for order items

**Fields:**
```java
Long productId;   // Product to order
Double qty;       // Quantity
```

### 3. **CustomerOrderResponseDto**
`Dto/CustomerOrderResponseDto.java`

**Purpose:** Output DTO for order responses

**Fields:**
```java
Long orderId;
String tableName;
Double totalAmount;
String paymentStatus;
String orderStatus;
LocalDateTime createdAt;
List<CustomerOrderItemResponseDto> items;
```

### 4. **CustomerOrderItemResponseDto**
`Dto/CustomerOrderItemResponseDto.java`

**Purpose:** Output DTO for order items

**Fields:**
```java
Long id;
String productName;
Double qty;
Double price;
Double total;
```

## Service Layer

### 1. **TableMasterService**
`Service/TableMasterService.java`

**Methods:**

#### createTable(TableMaster table)
- Validates tenant context
- Sets tenant ID automatically
- Sets default status to ACTIVE
- Returns created table

#### updateTable(Long tableId, TableMaster tableDetails)
- Validates table exists
- Verifies tenant ownership
- Updates specific fields (tableName, qrUrl, status)
- Returns updated table

#### getAllTables()
- Returns all tables for the current tenant
- Validates tenant context

#### getTableById(Long tableId)
- Retrieves specific table
- Validates tenant ownership
- Throws `ResourceNotFoundException` if not found

#### deleteTable(Long tableId)
- Validates tenant ownership
- Cascades delete to related orders
- Returns boolean success

**Exception Handling:**
- `InvalidTableException`: Tenant context missing or tenant mismatch
- `ResourceNotFoundException`: Table not found

### 2. **CustomerOrderService**
`Service/CustomerOrderService.java`

**Methods:**

#### createOrder(CreateCustomerOrderDto orderDto)
**Business Logic:**
1. Validates tenant context
2. Validates table exists and belongs to tenant
3. Validates order contains items
4. For each item:
   - Validates product exists
   - Retrieves product details
   - Calculates line item total
   - Creates CustomerOrderItem entity
5. Calculates grand total
6. Saves order and all items
7. Returns CustomerOrderResponseDto

#### getOrderById(Long orderId)
- Retrieves order by ID
- Validates tenant ownership
- Returns CustomerOrderResponseDto

#### getKitchenOrders()
**Returns:**
- All PENDING and CONFIRMED orders for the tenant
- Filtered by tenant context
- Useful for kitchen display systems

#### updateOrderStatus(Long orderId, String orderStatus)
- Validates order exists
- Validates status is one of: PENDING, CONFIRMED, PREPARING, READY, SERVED, CANCELLED
- Updates order status
- Sets updated timestamp
- Returns updated CustomerOrderResponseDto

#### getCustomerOrderStatus(Long orderId)
- Returns current order status for customer
- Validates order belongs to tenant
- Returns CustomerOrderResponseDto

**Exception Handling:**
- `InvalidOrderException`: Tenant context missing or invalid data
- `InvalidTableException`: Table not found or tenant mismatch
- `ResourceNotFoundException`: Product or Order not found

## Controller Layer

### 1. **TableMasterController**
`Controller/TableMasterController.java`

**Base Path:** `/table`

**Endpoints:**

#### POST /table/register
Create a new table
```json
Request: { "tableName": "T1", "qrUrl": "https://..." }
Response: { "tableId": 1, "tableName": "T1", "status": "ACTIVE", ... }
```

#### GET /table/all
Get all tables for tenant
```json
Response: [ { "tableId": 1, ... }, { "tableId": 2, ... } ]
```

#### GET /table/{id}
Get specific table
```json
Response: { "tableId": 1, "tableName": "T1", ... }
```

#### PUT /table/{id}
Update table details
```json
Request: { "tableName": "T1-New", "status": "INACTIVE" }
Response: { "tableId": 1, "tableName": "T1-New", "status": "INACTIVE", ... }
```

#### DELETE /table/{id}
Delete table
```json
Response: "Table deleted successfully"
```

**Security:** Requires ADMIN or MANAGER role

### 2. **CustomerOrderController**
`Controller/CustomerOrderController.java`

**Base Path:** `/customer-order`

**Endpoints:**

#### GET /customer-menu/products
Get all available products (Public API)
```json
Response: [
  { "itemid": 1, "itemname": "Pizza", "price": 500, ... },
  { "itemid": 2, "itemname": "Burger", "price": 300, ... }
]
```

#### POST /customer-order/create
Create a new order (Public API)
```json
Request: {
  "tableId": 1,
  "items": [
    { "productId": 1, "qty": 2 },
    { "productId": 2, "qty": 1 }
  ]
}
Response: {
  "orderId": 1,
  "tableName": "T1",
  "totalAmount": 1300,
  "paymentStatus": "PENDING",
  "orderStatus": "PENDING",
  "createdAt": "2024-01-15T10:30:00",
  "items": [
    { "id": 1, "productName": "Pizza", "qty": 2, "price": 500, "total": 1000 },
    { "id": 2, "productName": "Burger", "qty": 1, "price": 300, "total": 300 }
  ]
}
```

#### GET /customer-order/status/{orderId}
Get order status (Public API)
```json
Response: { "orderId": 1, "tableName": "T1", "orderStatus": "PREPARING", ... }
```

**Security:** `/customer-menu/products`, `/customer-order/create`, `/customer-order/status/**` are public (no authentication required)

### 3. **KitchenController**
`Controller/KitchenController.java`

**Base Path:** `/kitchen`

**Endpoints:**

#### GET /kitchen/orders
Get all pending and confirmed orders (Protected - KITCHEN role)
```json
Response: [
  { "orderId": 1, "tableName": "T1", "orderStatus": "PENDING", ... },
  { "orderId": 2, "tableName": "T3", "orderStatus": "CONFIRMED", ... }
]
```

#### PUT /kitchen/order/{orderId}/status
Update order status (Protected - KITCHEN role)
```
Query Parameter: status=PREPARING|READY|SERVED|CANCELLED

Response: { "orderId": 1, "orderStatus": "PREPARING", ... }
```

**Security:** Requires ADMIN, KITCHEN, or MANAGER role

## Security Configuration

### Updated SecurityConfig
`Configuration/SecurityConfig.java`

**Public Endpoints (No Authentication Required):**
```
/customer-menu/**
/customer-order/create
/customer-order/status/**
/auth/login
/auth/signup
/auth/refresh
/tenant/create
/tenant/login
```

**Protected Endpoints:**
- `/table/**` - Requires ADMIN or MANAGER role
- `/kitchen/**` - Requires ADMIN, KITCHEN, or MANAGER role
- `/products/**`, `/branches/**`, `/users/**`, `/stocktransfer/**` - Requires ADMIN, CASHIER, MANAGER, or KITCHEN role

### Multi-Tenant Header
All protected APIs require the `X-Tenant-ID` header:
```
X-Tenant-ID: tenant-123
```

The `TenantFilter` automatically extracts this header and sets the tenant context.

## Exception Handling

### Custom Exceptions

#### InvalidTableException
- Thrown when table validation fails
- Tenant context missing
- Table doesn't belong to tenant

#### InvalidOrderException
- Thrown when order creation fails
- Invalid order status
- Empty order items
- Tenant context missing

#### ResourceNotFoundException (Existing)
- Thrown when resource not found
- Product doesn't exist
- Order/Table doesn't exist

## Database Migration

### V2__create_ordering_tables.sql
`resources/db/migration/V2__create_ordering_tables.sql`

**Creates:**
1. `table_master` - Stores restaurant tables
2. `customer_order` - Stores customer orders
3. `customer_order_item` - Stores order line items

**Indexes:**
- Tenant ID indexes for multi-tenant queries
- Status indexes for filtering
- Foreign key indexes for joins

## Usage Example

### 1. Create Table
```bash
curl -X POST http://localhost:8080/table/register \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant-123" \
  -H "Authorization: Bearer <jwt_token>" \
  -d '{
    "tableName": "Table 1",
    "qrUrl": "https://qr.example.com/table/1"
  }'
```

### 2. Get Menu (Public)
```bash
curl -X GET http://localhost:8080/customer-menu/products \
  -H "X-Tenant-ID: tenant-123"
```

### 3. Create Order (Public)
```bash
curl -X POST http://localhost:8080/customer-order/create \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant-123" \
  -d '{
    "tableId": 1,
    "items": [
      { "productId": 1, "qty": 2 },
      { "productId": 2, "qty": 1 }
    ]
  }'
```

### 4. Check Order Status (Public)
```bash
curl -X GET http://localhost:8080/customer-order/status/1 \
  -H "X-Tenant-ID: tenant-123"
```

### 5. Get Kitchen Orders (Protected)
```bash
curl -X GET http://localhost:8080/kitchen/orders \
  -H "X-Tenant-ID: tenant-123" \
  -H "Authorization: Bearer <jwt_token>"
```

### 6. Update Order Status (Protected)
```bash
curl -X PUT "http://localhost:8080/kitchen/order/1/status?status=PREPARING" \
  -H "X-Tenant-ID: tenant-123" \
  -H "Authorization: Bearer <jwt_token>"
```

## Data Flow

### Order Creation Flow
```
1. Customer submits order via POST /customer-order/create
   - Contains tableId and list of items
   
2. CustomerOrderService.createOrder() processes:
   - Validates table exists and belongs to tenant
   - Validates each product exists
   - Calculates line item totals
   - Calculates order total
   
3. Creates CustomerOrder entity with status=PENDING
   
4. Creates CustomerOrderItem entities for each item
   
5. Returns CustomerOrderResponseDto with order details
```

### Kitchen Workflow
```
1. Kitchen staff retrieves pending orders: GET /kitchen/orders
   - Returns PENDING and CONFIRMED orders
   
2. Kitchen staff prepares order
   
3. Kitchen staff updates status: PUT /kitchen/order/{orderId}/status?status=PREPARING
   
4. As preparation progresses:
   - Status changes to READY when ready for serving
   - Status changes to SERVED after served to customer
   
5. Customer can track progress via GET /customer-order/status/{orderId}
```

## Best Practices

1. **Always Include X-Tenant-ID Header:** Even for public endpoints, include the tenant ID
2. **Use JWT Authentication:** Protected endpoints require valid JWT token
3. **Handle Exceptions:** Implement error handling for InvalidTableException and InvalidOrderException
4. **Validate Input:** Ensure item quantities are positive numbers
5. **Track Order Status:** Use order status updates to synchronize with POS system
6. **Implement QR Codes:** Use tableName and QR URL to link physical tables to digital ordering

## Future Enhancements

1. **Payment Processing:** Integrate payment gateway for payment completion
2. **Order Cancellation:** Add business logic for cancelling items from orders
3. **Inventory Management:** Deduct product quantities from stock during order creation
4. **Order Modifications:** Allow customers to modify orders before confirmation
5. **Table Merging:** Support merging orders from multiple tables
6. **Analytics:** Track sales, popular items, peak hours by tenant
7. **Notifications:** Real-time WebSocket updates for kitchen and customers
8. **Discounts/Offers:** Apply promotional discounts to orders
