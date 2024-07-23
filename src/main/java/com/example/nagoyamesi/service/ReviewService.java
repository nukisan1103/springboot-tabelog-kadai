package com.example.nagoyamesi.service;

import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReviewEditForm;
import com.example.nagoyamesi.form.ReviewForm;
import com.example.nagoyamesi.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;

	}

	@Transactional //レビュー登録用
	public void create(User user, Restaurant restaurants, ReviewForm reviewForm) {

		Review review = new Review();

		review.setUser(user);
		review.setRestaurant(restaurants);
		review.setScore(reviewForm.getScore());
		review.setSentense(reviewForm.getSentense());

		reviewRepository.save(review);
	}
	
	@Transactional //レビュー編集用
	public void update(Integer id, ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(id);
		
		review.setScore(reviewEditForm.getScore());
		review.setSentense(reviewEditForm.getSentense());
		
		reviewRepository.save(review);
		
	}
	@Transactional //レビュー削除用
	public void deleteReview(Integer id) {
		
		reviewRepository.deleteById(id);
		
	}
}