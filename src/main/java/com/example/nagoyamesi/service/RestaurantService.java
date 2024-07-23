package com.example.nagoyamesi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.form.RestaurantEditForm;
import com.example.nagoyamesi.form.RestaurantRegisterForm;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.repository.ReviewRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final ReservationRepository reservationRepository;

	public RestaurantService(RestaurantRepository restaurantRepository
			,ReviewRepository reviewRepository,ReservationRepository reservationRepository) {
		this.restaurantRepository = restaurantRepository;
		this.reviewRepository = reviewRepository;
		this.reservationRepository = reservationRepository;
		
	}

	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			restaurant.setImage_name(hashedImageName);
		}
		restaurant.setCategoryName(restaurantRegisterForm.getCategory_name());
		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getRestaurants_description());
		restaurant.setLowest_price(restaurantRegisterForm.getLowest_price());
		restaurant.setHighest_price(restaurantRegisterForm.getHighest_price());
		restaurant.setOpening_time(restaurantRegisterForm.getOpening_time());
		restaurant.setClosing_time(restaurantRegisterForm.getClosing_time());
		restaurant.setRegular_holiday(restaurantRegisterForm.getRegular_holiday());
		restaurant.setCapacity(restaurantRegisterForm.getCapacity());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhone_number(restaurantRegisterForm.getPhone_number());

		restaurantRepository.save(restaurant);
	}

	@Transactional
	public void update(RestaurantEditForm restaurantEditForm) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
		MultipartFile imageFile = restaurantEditForm.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			restaurant.setImage_name(hashedImageName);
		}

		restaurant.setName(restaurantEditForm.getName());
		restaurant.setCategoryName(restaurantEditForm.getCategory_name());
		restaurant.setDescription(restaurantEditForm.getRestaurants_description());
		restaurant.setLowest_price(restaurantEditForm.getLowest_price());
		restaurant.setHighest_price(restaurantEditForm.getHighest_price());
		restaurant.setOpening_time(restaurantEditForm.getOpening_time());
		restaurant.setClosing_time(restaurantEditForm.getClosing_time());
		restaurant.setRegular_holiday(restaurantEditForm.getRegular_holiday());
		restaurant.setCapacity(restaurantEditForm.getCapacity());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhone_number(restaurantEditForm.getPhone_number());

		restaurantRepository.save(restaurant);
	}

	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional //店舗削除処理
	public void deleteRestaurant(Restaurant restaurant) {
		
		reservationRepository.deleteByRestaurant(restaurant);
		
		reviewRepository.deleteByRestaurant(restaurant);
		
		restaurantRepository.deleteById(restaurant.getId());
				
	}

}