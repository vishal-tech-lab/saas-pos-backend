package com.example.Backend.Service;

import com.example.Backend.Entity.ItemCategory;
import com.example.Backend.Repository.ItemCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemCategoryService {

    private final ItemCategoryRepository repository;

    @Autowired
    public ItemCategoryService(ItemCategoryRepository repository) {
        this.repository = repository;
    }

    public ItemCategory create(ItemCategory itemCategory) {
        return repository.save(itemCategory);
    }

    public List<ItemCategory> getAll() {
        return repository.findAll();
    }

    public ItemCategory update(Long id, ItemCategory itemCategory) {
        return repository.findById(id).map(existing -> {
            existing.setItemcategoryname(itemCategory.getItemcategoryname());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("ItemCategory not found with id: " + id));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ItemCategory not found with id: " + id);
        }
        repository.deleteById(id);
    }

}
