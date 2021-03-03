package com.example.finalpj.reposiroty;

import com.example.finalpj.dto.CategoryDto;
import com.example.finalpj.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(value = "select c from Category c")
    List<CategoryDto> findAllDto();

    @Query(value = "select c from Category c where c.id = ?1")
    CategoryDto findDtoById(String id);
}
