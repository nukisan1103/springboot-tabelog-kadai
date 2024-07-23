
package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Category;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.repository.CategoryRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;


@Controller
@RequestMapping(value = {"/restaurants"})
public class RestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;

	public RestaurantController(RestaurantRepository restaurantRepository,CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;

	}
	
	//無料会員用店舗一覧and店舗検索ページ
	@GetMapping
	public String generalIndex(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Restaurant> restaurants;
		List<Category> categories;
		categories = categoryRepository.findAll();
		
		if (keyword != null && !keyword.isEmpty()) {

			restaurants = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);

		} else {

			restaurants = restaurantRepository.findAll(pageable);

		}

		model.addAttribute("categories", categories);
		model.addAttribute("restaurants", restaurants);
		model.addAttribute("keyword", keyword);

		return "general/restaurants/index";
	}
	
	//無料会員用店舗詳細ページ
	@GetMapping("/{id}")
	public String generalShow(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurants = restaurantRepository.getReferenceById(id);

		model.addAttribute("restaurants", restaurants);

		return "general/restaurants/show";
	}
	

	//店舗一覧ページでカテゴリ検索を実施した際の処理
	@GetMapping("/categorySearch")
	public String categorySearch(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "category", required = false) String category
			
			) {
		Page<Restaurant> restaurants;
		List<Category> categories = categoryRepository.findAll();
					
		if (category != null && !category.isEmpty()) {
		//完全一致でなくとも、検索文字が含まれていればリストに追加
		restaurants = restaurantRepository.findByCategoryName(category, pageable);

		} else {

			restaurants = restaurantRepository.findAll(pageable);
			
		}
		
		model.addAttribute("categories", categories);
		model.addAttribute("restaurants", restaurants);
		model.addAttribute("keyword", keyword);

		return "general/restaurants/index";
	}
	

}
