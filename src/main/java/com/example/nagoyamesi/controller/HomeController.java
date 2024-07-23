
package com.example.nagoyamesi.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Category;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.example.nagoyamesi.repository.CategoryRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.repository.ReviewRepository;

@Controller
public class HomeController {

	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;

	public HomeController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
			CategoryRepository categoryRepository) {

		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;

	}

	//トップページ遷移用
	@GetMapping("/")
	public String index(Model model) {

		List<Restaurant> restaurants = restaurantRepository.findAll();
		Map<Restaurant, Double> scoreAvg = new HashMap<>();

		for (Restaurant restaurant : restaurants) {
		    List<Review> reviews = reviewRepository.findByRestaurant(restaurant);
		    if (!reviews.isEmpty()) {
		        // トータルスコアを算出し、平均スコアを計算
		        double avg = reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
		        scoreAvg.put(restaurant, avg);
		    }
		}

		// 作成したMapを値（平均スコア）の降順でソートし、キー（Restaurant）を取得
		List<Map.Entry<Restaurant, Double>> scoreSort = scoreAvg.entrySet().stream()
		    .sorted(Map.Entry.<Restaurant, Double>comparingByValue(Comparator.reverseOrder()))
		    .collect(Collectors.toList());

		model.addAttribute("scoreSort", scoreSort);

		//新着順に店舗をソートし、上位六位までのデータをビューに送信
		List<Restaurant> restaurantList = restaurantRepository.findTop6ByOrderByCreatedAtDesc();

		model.addAttribute("restaurants", restaurantList);

		//カテゴリーを全権取得
		List<Category> categories = categoryRepository.findAll();

		model.addAttribute("categories", categories);

		return "index";
	}

	//トップページの店舗名をクリックした際の店舗詳細ページ表示
	@GetMapping("/top/{id}")
	public String topSearch(Model model, @PathVariable(name = "id") String keyword) {

		Restaurant restaurants = restaurantRepository.findByName(keyword);
		model.addAttribute("restaurants", restaurants);
		model.addAttribute("reservationInputForm", new ReservationInputForm());

		return "top/show";

	}

	//トップページでの店舗検索結果表示
	@GetMapping("/top/search")
	public String topWordSearch(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Restaurant> restaurants;
		restaurants = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
		if (restaurants.getTotalPages() == 0) {
			model.addAttribute("errorMessage", "検索結果はありません。");
		} else {

			model.addAttribute("restaurants", restaurants);
		}

		return "top/search";
	}

	//会社概要ページへ
	@GetMapping("/top/category/{id}")
	public String topCategorySearch(Model model, @PathVariable(name = "id") String keyword) {

		List<Restaurant> restaurants = restaurantRepository.findByCategoryName(keyword);
		model.addAttribute("restaurants", restaurants);

		return "top/search";
	}

	//利用規約ページへ
	@GetMapping("top/information")
	public String informationPage() {

		return "information/company";
	}

	//会社情報ページへ
	@GetMapping("top/terms")
	public String terms() {

		return "information/term";
	}
}
