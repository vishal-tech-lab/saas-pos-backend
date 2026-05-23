package com.example.Backend.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Entity.Expensecategory;
import com.example.Backend.Service.Expensecategoryservice;

@RestController
@RequestMapping("expcatgory")
public class Expensecategorycontroller {
          
    @Autowired
    private Expensecategoryservice expensecategoryservice;

    @PostMapping("register")
    public String addtotalcatogry(@RequestBody Expensecategory expensecategory){
        return expensecategoryservice.getthetotalcatogry(expensecategory);
    }
    
    @GetMapping("all")
    public List<Expensecategory> getallcatogry(){
        return expensecategoryservice.getallcatogry();
    }

    // ✅ NEW: Update Category
    @PutMapping("update/{id}")
public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody Expensecategory expensecategory) {
    String result = expensecategoryservice.updateCategory(id, expensecategory);
    if(result.equals("successfully updated")) {
        return ResponseEntity.ok(result);
    }
    return ResponseEntity.status(404).body(result);
}

    // ✅ NEW: Delete Category
    @DeleteMapping("delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        return expensecategoryservice.deleteCategory(id);
    }
}