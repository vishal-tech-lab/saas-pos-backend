package com.example.Backend.Controller;

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
import com.example.Backend.Entity.Customer;
import com.example.Backend.Service.Customerservice;


@RestController
@RequestMapping("customer")
public class Customercontroller {
@Autowired
Customerservice customerservice;

@PostMapping("add")    
public String savecustoemrs( @RequestBody Customer customer){
    return  customerservice.savecustomer(customer);
}
@GetMapping("all")
public List<Customer> getallcustomer(){
   return customerservice.getallcustomer();
}
@PutMapping("{id}")
public Customer updatecustomer(@PathVariable Long id,@RequestBody Customer customer){
    return customerservice.updatecustomer(id,customer);
}
@DeleteMapping("{id}")
public String deletecustomer(@PathVariable Long id){
   return customerservice.deletecustomer(id );
}
@GetMapping("/")
public String Wleocme(){
    return "HOME PAGE";     
} 
}
