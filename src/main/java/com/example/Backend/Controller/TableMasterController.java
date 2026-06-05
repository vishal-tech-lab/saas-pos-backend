package com.example.Backend.Controller;

import com.example.Backend.Entity.TableMaster;
import com.example.Backend.Service.TableMasterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/table")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TableMasterController {

    private final TableMasterService tableMasterService;

    /**
     * Register a new table
     * POST /table/register
     */
    @PostMapping("/register")
    public ResponseEntity<TableMaster> registerTable(@RequestBody TableMaster table) {
        TableMaster savedTable = tableMasterService.createTable(table);
        return new ResponseEntity<>(savedTable, HttpStatus.CREATED);
    }

    /**
     * Get all tables
     * GET /table/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<TableMaster>> getAllTables() {
        List<TableMaster> tables = tableMasterService.getAllTables();
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }

    /**
     * Get table by ID
     * GET /table/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TableMaster> getTableById(@PathVariable Long id) {
        TableMaster table = tableMasterService.getTableById(id);
        return new ResponseEntity<>(table, HttpStatus.OK);
    }

    /**
     * Update table
     * PUT /table/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableMaster> updateTable(@PathVariable Long id, @RequestBody TableMaster tableDetails) {
        TableMaster updatedTable = tableMasterService.updateTable(id, tableDetails);
        return new ResponseEntity<>(updatedTable, HttpStatus.OK);
    }

    /**
     * Delete table
     * DELETE /table/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTable(@PathVariable Long id) {
        boolean deleted = tableMasterService.deleteTable(id);
        if (deleted) {
            return new ResponseEntity<>("Table deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Table not found", HttpStatus.NOT_FOUND);
    }
}
