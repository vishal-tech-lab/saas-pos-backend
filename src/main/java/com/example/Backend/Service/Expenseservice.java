package com.example.Backend.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Expense;
import com.example.Backend.Repository.Expenserepo;
@Service
public class Expenseservice {
    

    @Autowired
    private Expenserepo expenserepo;

    public String addthetotalexpense(Expense expense) {
            expenserepo.save(expense);   
            return "sucessfully updated"; 
        }
    public List<Expense> getallexpense(Expense expense) {                    
      return  expenserepo.findAll();      
    }
   public Expense updateallexpense(Long id, Expense expense) {
    return expenserepo.findById(id)
        .map(value -> {
            value.setAmount(expense.getAmount());
            value.setCategory(expense.getCategory());       // Add this
            value.setDescription(expense.getDescription()); // Add this
            value.setDate(expense.getDate());               // Add this
            return expenserepo.save(value);
        })
        .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
}
    public String deleteallexpense(Long id) {
     expenserepo.deleteById(id);   
     return("sucessfully updated");

}
     public List<Expense> findByDate(LocalDate date) {
        return expenserepo.findByDate(date);
    }
     
}
