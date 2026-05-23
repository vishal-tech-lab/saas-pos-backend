package com.example.Backend.Controller;

import com.example.Backend.Entity.ItemCategory;
import com.example.Backend.Service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemcategory")
public class ItemCategoryController {

    private final ItemCategoryService service;

    @Autowired
    public ItemCategoryController(ItemCategoryService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ItemCategory> register(@RequestBody ItemCategory itemCategory) {
        ItemCategory saved = service.create(itemCategory);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemCategory>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemCategory> update(@PathVariable Long id, @RequestBody ItemCategory itemCategory) {
        try {
            ItemCategory updated = service.update(id, itemCategory);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
