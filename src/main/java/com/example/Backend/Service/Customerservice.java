package com.example.Backend.Service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Customer;
import com.example.Backend.Repository.Customerrepo;



@Service
public class Customerservice {
    @Autowired
    Customerrepo customerrepo;
  public String savecustomer( Customer customer){
        
    customerrepo.save(customer);
    return "Suceesfull customer saved";

  }
  public List<Customer> getallcustomer() {
    return customerrepo.findAll();
  }
  public Customer  updatecustomer(Long id, Customer customer) {
    return customerrepo.findById(id).map(
      customers->{
       customers.setCustomername(customer.getCustomername());
             return customerrepo.save(customers);

      })
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

    
  }

  public String deletecustomer(Long id) {
   return  customerrepo.findById(id).map(
      customers->{
             customerrepo.deleteById(id);     
             return "customer sucessfully deleted";       
      })
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }
 

}  
