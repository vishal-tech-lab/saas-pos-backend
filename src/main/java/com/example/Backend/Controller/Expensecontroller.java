package com.example.Backend.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Entity.Expense;
import com.example.Backend.Service.Expenseservice;

@RestController
@RequestMapping("expense")
public class Expensecontroller {
    
    @Autowired
    private Expenseservice expenseservice;

    @PostMapping("register")
    public String addthetotalexpense(@RequestBody Expense expense){
        return expenseservice.addthetotalexpense(expense);

    }

    @GetMapping("all")
    public List<Expense> getallexpense(Expense expense){
        return expenseservice.getallexpense(expense);
    }
    
    @PutMapping("update/{id}")
    public Expense updateallexpense(@PathVariable Long id,@RequestBody Expense expense){
        return expenseservice.updateallexpense(id,expense);
    }
    
    @DeleteMapping("delete/{id}")
    public String deleteallexpense(@PathVariable Long id){
        return expenseservice.deleteallexpense(id);
    }
  @GetMapping("/date/{date}")
    public List<Expense> getExpensesByDate(@PathVariable("date") String date) {
        // Converts "2026-04-17" String to LocalDate object
        LocalDate localDate = LocalDate.parse(date); 
        return expenseservice.findByDate(localDate);
    
}
    
}
