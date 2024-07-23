package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	public Favorite findByUserAndRestaurant(int userId, Integer integer);

	public List<Favorite> findByUser(int userData);

	public Favorite findByRestaurant(int userData);

	

}