package com.example.Backend.Service;

import com.example.Backend.Entity.TableMaster;
import com.example.Backend.Exception.ResourceNotFoundException;
import com.example.Backend.Exception.InvalidTableException;
import com.example.Backend.Repository.TableMasterRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TableMasterService {

    private final TableMasterRepository tableMasterRepository;

    /**
     * Create a new table
     */
    public TableMaster createTable(TableMaster table) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidTableException("Tenant context not set");
        }
if (table.getStatus() == null) {
    table.setStatus("AVAILABLE");
}        return tableMasterRepository.save(table);
    }

    /**
     * Update an existing table
     */
    public TableMaster updateTable(Long tableId, TableMaster tableDetails) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidTableException("Tenant context not set");
        }

        Optional<TableMaster> optionalTable = tableMasterRepository.findById(tableId);
        if (optionalTable.isEmpty()) {
            throw new ResourceNotFoundException("Table not found with id: " + tableId);
        }

        TableMaster table = optionalTable.get();

        if (tableDetails.getTableName() != null) {
            table.setTableName(tableDetails.getTableName());
        }
        if (tableDetails.getQrUrl() != null) {
            table.setQrUrl(tableDetails.getQrUrl());
        }
        if (tableDetails.getStatus() != null) {
            table.setStatus(tableDetails.getStatus());
        }

        return tableMasterRepository.save(table);
    }

    /**
     * Get all tables for the tenant
     */
    public List<TableMaster> getAllTables() {
        if (TenantContext.getTenant() == null) {
            throw new InvalidTableException("Tenant context not set");
        }
        return tableMasterRepository.findAll();
    }

    /**
     * Get table by ID
     */
    public TableMaster getTableById(Long tableId) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidTableException("Tenant context not set");
        }

        Optional<TableMaster> table = tableMasterRepository.findById(tableId);
        if (table.isEmpty()) {
            throw new ResourceNotFoundException("Table not found with id: " + tableId);
        }

        return table.get();
    }

    /**
     * Delete a table
     */
    public boolean deleteTable(Long tableId) {
        if (TenantContext.getTenant() == null) {
            throw new InvalidTableException("Tenant context not set");
        }

        Optional<TableMaster> table = tableMasterRepository.findById(tableId);
        if (table.isEmpty()) {
            return false;
        }

        tableMasterRepository.deleteById(tableId);
        return true;
    }
}
