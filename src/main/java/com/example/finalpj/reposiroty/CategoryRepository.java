package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
