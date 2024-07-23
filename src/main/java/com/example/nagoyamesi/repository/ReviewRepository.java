package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	 public Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	 
	 public Page<Review> findByRestaurant(Restaurant restaurant,Pageable pageable);
	 
	public void deleteByRestaurant(Restaurant restaurant);
		
	public List<Review> findTop6AllByOrderByScoreDesc();
	
	public void deleteByUser(User user);

	public List<Review> findByRestaurant(Restaurant restaurant);

	public Review findByUserAndRestaurant(User user, Restaurant restaurants);

	public Page<Review> findByUser(User user, Pageable pageable);
	
	
	}