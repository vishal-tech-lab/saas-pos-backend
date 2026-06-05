#!/bin/bash

# ========================================
# Restaurant Ordering System - API Test Cases
# ========================================

# Base URL
BASE_URL="http://localhost:8080"
TENANT_ID="tenant-123"

# JWT Token (replace with actual token)
JWT_TOKEN="your_jwt_token_here"

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Restaurant Ordering System - API Tests${NC}"
echo -e "${BLUE}========================================${NC}"

# ========================================
# 1. TABLE MANAGEMENT TESTS
# ========================================
echo -e "\n${GREEN}1. TABLE MANAGEMENT TESTS${NC}"

# Create Table 1
echo -e "\n${BLUE}Creating Table 1...${NC}"
curl -X POST "$BASE_URL/table/register" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "tableName": "Table 1",
    "qrUrl": "https://qr.example.com/table/1",
    "status": "ACTIVE"
  }' | jq .

# Create Table 2
echo -e "\n${BLUE}Creating Table 2...${NC}"
curl -X POST "$BASE_URL/table/register" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "tableName": "Table 2",
    "qrUrl": "https://qr.example.com/table/2",
    "status": "ACTIVE"
  }' | jq .

# Get All Tables
echo -e "\n${BLUE}Getting all tables...${NC}"
curl -X GET "$BASE_URL/table/all" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Get Specific Table
echo -e "\n${BLUE}Getting Table 1 details...${NC}"
curl -X GET "$BASE_URL/table/1" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Update Table
echo -e "\n${BLUE}Updating Table 1 status...${NC}"
curl -X PUT "$BASE_URL/table/1" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "status": "RESERVED"
  }' | jq .

# ========================================
# 2. CUSTOMER ORDERING TESTS (PUBLIC)
# ========================================
echo -e "\n${GREEN}2. CUSTOMER ORDERING TESTS (PUBLIC)${NC}"

# Get Menu Products
echo -e "\n${BLUE}Getting menu products (Public API)...${NC}"
curl -X GET "$BASE_URL/customer-menu/products" \
  -H "X-Tenant-ID: $TENANT_ID" | jq .

# Create Order
echo -e "\n${BLUE}Creating new order...${NC}"
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/customer-order/create" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{
    "tableId": 1,
    "items": [
      { "productId": 1, "qty": 2 },
      { "productId": 2, "qty": 1 }
    ]
  }')
echo $ORDER_RESPONSE | jq .
ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.orderId')

# Check Order Status (Public)
echo -e "\n${BLUE}Checking order status (Public API)...${NC}"
curl -X GET "$BASE_URL/customer-order/status/$ORDER_ID" \
  -H "X-Tenant-ID: $TENANT_ID" | jq .

# ========================================
# 3. KITCHEN OPERATIONS TESTS
# ========================================
echo -e "\n${GREEN}3. KITCHEN OPERATIONS TESTS (PROTECTED)${NC}"

# Get Kitchen Orders
echo -e "\n${BLUE}Getting kitchen orders (Protected API)...${NC}"
curl -X GET "$BASE_URL/kitchen/orders" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Update Order Status - CONFIRMED
echo -e "\n${BLUE}Updating order status to CONFIRMED...${NC}"
curl -X PUT "$BASE_URL/kitchen/order/$ORDER_ID/status?status=CONFIRMED" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Update Order Status - PREPARING
echo -e "\n${BLUE}Updating order status to PREPARING...${NC}"
curl -X PUT "$BASE_URL/kitchen/order/$ORDER_ID/status?status=PREPARING" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Update Order Status - READY
echo -e "\n${BLUE}Updating order status to READY...${NC}"
curl -X PUT "$BASE_URL/kitchen/order/$ORDER_ID/status?status=READY" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Update Order Status - SERVED
echo -e "\n${BLUE}Updating order status to SERVED...${NC}"
curl -X PUT "$BASE_URL/kitchen/order/$ORDER_ID/status?status=SERVED" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# ========================================
# 4. ADDITIONAL ORDER TESTS
# ========================================
echo -e "\n${GREEN}4. ADDITIONAL ORDER TESTS${NC}"

# Create Second Order at Table 2
echo -e "\n${BLUE}Creating second order at Table 2...${NC}"
ORDER2_RESPONSE=$(curl -s -X POST "$BASE_URL/customer-order/create" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{
    "tableId": 2,
    "items": [
      { "productId": 1, "qty": 1 },
      { "productId": 3, "qty": 2 }
    ]
  }')
echo $ORDER2_RESPONSE | jq .
ORDER2_ID=$(echo $ORDER2_RESPONSE | jq -r '.orderId')

# Get Kitchen Orders Again
echo -e "\n${BLUE}Getting kitchen orders again...${NC}"
curl -X GET "$BASE_URL/kitchen/orders" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# ========================================
# 5. ERROR HANDLING TESTS
# ========================================
echo -e "\n${GREEN}5. ERROR HANDLING TESTS${NC}"

# Try to create order with non-existent table
echo -e "\n${BLUE}Creating order with non-existent table (should fail)...${NC}"
curl -X POST "$BASE_URL/customer-order/create" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{
    "tableId": 999,
    "items": [
      { "productId": 1, "qty": 1 }
    ]
  }' | jq .

# Try to create order with empty items
echo -e "\n${BLUE}Creating order with empty items (should fail)...${NC}"
curl -X POST "$BASE_URL/customer-order/create" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -d '{
    "tableId": 1,
    "items": []
  }' | jq .

# Try to update non-existent order
echo -e "\n${BLUE}Updating non-existent order (should fail)...${NC}"
curl -X PUT "$BASE_URL/kitchen/order/999/status?status=PREPARING" \
  -H "X-Tenant-ID: $TENANT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq .

# Try to delete table without authorization
echo -e "\n${BLUE}Deleting table without token (should fail)...${NC}"
curl -X DELETE "$BASE_URL/table/1" \
  -H "X-Tenant-ID: $TENANT_ID" | jq .

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}API Tests Completed${NC}"
echo -e "${GREEN}========================================${NC}"
