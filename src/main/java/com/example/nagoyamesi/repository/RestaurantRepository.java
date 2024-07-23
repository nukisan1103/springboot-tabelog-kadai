package com.example.nagoyamesi.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Restaurant;


 public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	 
	 public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);


	public Page<Restaurant> findByCategoryName(String category, Pageable pageable);


	public Restaurant findByName(String keyword);


	public List<Restaurant> findTop6ByOrderByCreatedAtDesc();


	public List<Restaurant> findByCategoryName(String keyword);


	public List<Restaurant> findByNameAndId(String name, int userData);


	
	
	
 }