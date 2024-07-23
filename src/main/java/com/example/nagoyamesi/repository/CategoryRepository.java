package com.example.nagoyamesi.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	public Category findByCategory(String category_name);

	public List<Category> findByCategoryLike(String string);

}