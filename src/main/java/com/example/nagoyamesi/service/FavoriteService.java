package com.example.nagoyamesi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Favorite;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.repository.FavoriteRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;

	public FavoriteService(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
	}

	@Transactional  //お気に入り登録実行
	public void favoriteRegist(int userId, Integer restaurantId) {
		Favorite favorite = new Favorite();

		favorite.setUser(userId);
		favorite.setRestaurant(restaurantId);

		favoriteRepository.save(favorite);

	}

	//ユーザーが既に対象の店舗をお気に入り登録しているかどうか判定する
	public boolean isAlreadyRegist(int userId, Restaurant restaurants) {
		Favorite favorite = favoriteRepository.findByUserAndRestaurant(userId, restaurants.getId());
		if (favorite != null) {
			return true;
		}
		return false;
	}
	
	//ログイン中のユーザーのお気に入り店舗一覧表示用に、データを検索
	public List<Restaurant> getFavoriteRestaurantsByUserId(Integer userId) {
		List<Favorite> favorites = favoriteRepository.findByUser(userId);
		List<Integer> restaurantIds = favorites.stream()
				.map(Favorite::getRestaurant)
				.collect(Collectors.toList());
		return restaurantRepository.findAllById(restaurantIds);
	}

	@Transactional //お気に入り削除用メソッド
	public void favoriteDelete(Integer id, Integer userId) {
		Favorite searchFavoriteId = favoriteRepository.findByUserAndRestaurant(userId, id);
		favoriteRepository.deleteById(searchFavoriteId.getId());

	}

}