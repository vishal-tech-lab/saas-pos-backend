# Restaurant Ordering System - Integration Checklist

## Pre-Deployment Verification

### Code Quality Checks
- [ ] Run Maven compile: `./mvnw clean compile`
- [ ] Run Maven build: `./mvnw clean package`
- [ ] Fix any compilation errors
- [ ] Check for any Spring annotation warnings

### Database Setup
- [ ] Verify PostgreSQL is running
- [ ] Verify Flyway migrations path is correct
- [ ] Run migrations: `./mvnw flyway:migrate`
- [ ] Verify database tables are created:
  - [ ] table_master table exists
  - [ ] customer_order table exists
  - [ ] customer_order_item table exists
  - [ ] All indexes created

### Application Configuration
- [ ] Verify application.properties has correct database URL
- [ ] Verify multi-tenant database configuration
- [ ] Verify JWT secret is configured
- [ ] Verify CORS settings allow frontend origin

### Test Data Setup (Optional)
```sql
-- Insert test tables
INSERT INTO table_master (table_name, qr_url, status, tenant_id) VALUES
('Table 1', 'https://qr.example.com/1', 'ACTIVE', 'tenant-123'),
('Table 2', 'https://qr.example.com/2', 'ACTIVE', 'tenant-123'),
('Table 3', 'https://qr.example.com/3', 'ACTIVE', 'tenant-123');
```

### API Testing
- [ ] Test public endpoints (no auth required):
  - [ ] GET /customer-menu/products
  - [ ] POST /customer-order/create
  - [ ] GET /customer-order/status/{orderId}
  
- [ ] Test protected endpoints (auth required):
  - [ ] GET /table/all
  - [ ] POST /table/register
  - [ ] GET /kitchen/orders
  - [ ] PUT /kitchen/order/{orderId}/status

### Security Verification
- [ ] Verify JWT token generation works
- [ ] Verify X-Tenant-ID header handling
- [ ] Test ADMIN/MANAGER role access to /table/*
- [ ] Test KITCHEN role access to /kitchen/*
- [ ] Verify public endpoints don't require token
- [ ] Test with missing X-Tenant-ID header (should fail on protected endpoints)

### Documentation
- [ ] Review ORDERING_SYSTEM_DOCUMENTATION.md
- [ ] Review IMPLEMENTATION_SUMMARY.md
- [ ] Share API endpoints with frontend team
- [ ] Share error codes and HTTP status codes

### Deployment Steps

1. **Build Application**
   ```bash
   cd d:\Projects\saas\Backend
   ./mvnw clean package -DskipTests
   ```

2. **Run Database Migrations**
   ```bash
   ./mvnw flyway:migrate
   ```

3. **Start Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   OR if using Docker:
   ```bash
   docker build -t restaurant-backend .
   docker run -p 8080:8080 restaurant-backend
   ```

4. **Verify Health Check**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Frontend Integration Tasks
- [ ] Create table selection UI
- [ ] Create menu display page
- [ ] Create order form with product selection
- [ ] Create order tracking page
- [ ] Implement QR code scanning
- [ ] Add tenant ID to all API requests
- [ ] Add error handling for API responses
- [ ] Implement order status polling or WebSocket

### Kitchen Display System (KDS)
- [ ] Design kitchen order display
- [ ] Implement status update buttons
- [ ] Add order notifications/alerts
- [ ] Implement order completion flow
- [ ] Add multi-language support for order items

### Future Enhancements
- [ ] Implement payment processing integration
- [ ] Add WebSocket for real-time updates
- [ ] Add order modification endpoints
- [ ] Add order cancellation logic
- [ ] Implement inventory deduction
- [ ] Add analytics dashboard
- [ ] Implement promotional discounts
- [ ] Add table merging logic

### Monitoring & Logging
- [ ] Setup application logging
- [ ] Monitor database connection pool
- [ ] Track API response times
- [ ] Monitor error rates
- [ ] Setup alerts for critical errors

### Performance Optimization
- [ ] Test with load generator (JMeter/Gatling)
- [ ] Verify database index usage
- [ ] Check N+1 query problems
- [ ] Optimize lazy loading if needed
- [ ] Cache frequently accessed data if needed

## Support & Maintenance

### Common Issues & Solutions

**Issue: Tenant context not set**
- Solution: Verify X-Tenant-ID header is included in all requests

**Issue: Flyway migration fails**
- Solution: Check database connectivity and migration file syntax

**Issue: 401 Unauthorized on protected endpoints**
- Solution: Verify JWT token is valid and included in Authorization header

**Issue: 403 Forbidden on protected endpoints**
- Solution: Verify user has required role (ADMIN, MANAGER, or KITCHEN)

**Issue: Order total calculation is wrong**
- Solution: Check product prices in database and verify qty calculations

## Contact & Support

For issues or questions:
1. Check logs in application console
2. Review error response messages
3. Consult ORDERING_SYSTEM_DOCUMENTATION.md
4. Check test_api.sh for working examples

## Version History

- v1.0 - Initial implementation
  - Table management
  - Order creation
  - Kitchen operations
  - Multi-tenant support
  - Security configuration
