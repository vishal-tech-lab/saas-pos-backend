package com.example.Backend.Service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Item;
import com.example.Backend.Entity.TanglishToTamilConverter;
import com.example.Backend.Repository.Itemrepo;

@Service
public class Itemservice {
    
    @Autowired
    private Itemrepo itemrepo;

  @Autowired
private TanglishToTamilConverter tamilConverter;

    public String additems(Item item){
        itemrepo.save(item);
        return "Item Added Sucessfully";
    }
    public List<Item> getitems() {
      return itemrepo.findAll();
    }
    public String updateitems(Long id, Item item) {
        return itemrepo.findById(id)
        .map(value->{
            value.setItemname(item.getItemname());
            itemrepo.save(value);
            return "Sucessfully Updated";
        })
        .orElseThrow(()-> new RuntimeException("Customer not found" +id));
    }
    public String deleteitems(Long id) {
        return itemrepo.findById(id)
        .map(value->{itemrepo.deleteById(id);
        return "Item Sucessfully Deleted";
    })
        .orElseThrow(()->new RuntimeException("Cannot upadate the customer "+id));
    }
    


public List<Item> tangilshsearch(String query) {
           String tamilquery=   tamilConverter.converter(query);
              return itemrepo.findByItemnameContaining(tamilquery);

}


    

}
