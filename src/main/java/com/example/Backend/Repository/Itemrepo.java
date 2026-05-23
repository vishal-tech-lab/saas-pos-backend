package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Item;
@Repository
public interface Itemrepo extends JpaRepository<Item,Long>{

    List<Item> findByItemnameContaining(String query);

}
