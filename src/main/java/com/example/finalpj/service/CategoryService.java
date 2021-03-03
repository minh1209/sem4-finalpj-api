package com.example.finalpj.service;

import com.example.finalpj.dto.CategoryDto;
import com.example.finalpj.entity.Category;
import com.example.finalpj.reposiroty.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category save(Category c) {
        return categoryRepository.save(c);
    }

    public Optional<Category> findById(String id) {
        return categoryRepository.findById(id);
    }

    //    DTO
    public List<CategoryDto> findAllDto() {
        return categoryRepository.findAllDto();
    }

    public CategoryDto findDtoById(String id) {
        return categoryRepository.findDtoById(id);
    }
}
