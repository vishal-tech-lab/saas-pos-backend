package com.example.Backend.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Expensecategory;
import com.example.Backend.Repository.Expensecategoryrepo;

@Service
public class Expensecategoryservice {

    @Autowired
    private Expensecategoryrepo expensecategoryrepo;

    public String getthetotalcatogry(Expensecategory expensecategory) {
        expensecategoryrepo.save(expensecategory);
        return "successfully saved";
    }

    public List<Expensecategory> getallcatogry() {
        return expensecategoryrepo.findAll();
    }

    // ✅ NEW: Update Logic
    // ✅ Update Logic for your Service
public  String updateCategory(Long id, Expensecategory newDetails) {
    return expensecategoryrepo.findById(id).map(category -> {
        // 1. Update the name
        category.setExpensecategory(newDetails.getExpensecategory());
        
        // 2. CRITICAL: Ensure the ID is set on the object we are saving
        // This tells JPA "Use THIS specific row, do not make a new one"
        category.setExpensecategoryid(id); 
        
        expensecategoryrepo.save(category); 
        return "successfully updated";
    }).orElse("Category not found");
}

// ✅ Delete Logic for your Service
public String deleteCategory(Long id) {
    if (expensecategoryrepo.existsById(id)) {
        expensecategoryrepo.deleteById(id);
        return "Category deleted successfully";
    }
    return "Category not found";
}
}