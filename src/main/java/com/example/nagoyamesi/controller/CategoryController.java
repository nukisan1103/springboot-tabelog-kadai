

package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Category;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.repository.CategoryRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;


@Controller
@RequestMapping(value = {"/category"})
public class CategoryController {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;

	public CategoryController(RestaurantRepository restaurantRepository,CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;

	}
	
	@GetMapping("/categorySearch") //会員用店舗一覧で、カテゴリ検索を実施した際の処理
	public String categorySearch(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "category", required = false) String category
			
			) {
		Page<Restaurant> restaurants;
		List<Category> categories = categoryRepository.findAll();
					
		if (category != null && !category.isEmpty()) {

		restaurants = restaurantRepository.findByCategoryName(category, pageable);

		} else {

			restaurants = restaurantRepository.findAll(pageable);
			
		}		
		model.addAttribute("categories", categories);
		model.addAttribute("restaurants", restaurants);
		model.addAttribute("keyword", keyword);

		return "subscriber/restaurants/index";
	}
}
