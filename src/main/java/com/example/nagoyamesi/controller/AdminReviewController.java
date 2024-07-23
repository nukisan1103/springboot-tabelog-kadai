
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.repository.ReviewRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.service.ReviewService;

@Controller
@RequestMapping(value = { "/admin/review" })
public class AdminReviewController {
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final UserRepository userRepository;


	public AdminReviewController(RestaurantRepository restaurantRepository,
			UserRepository userRepository, ReviewRepository reviewRepository,
			ReviewService reviewService) {
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
	}

	
	@GetMapping //レビュー管理ページへ
	     public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable
	    		 ,@RequestParam(name = "keyword", required = false) String keyword,Model model) {
		Restaurant restaurant = restaurantRepository.findByName(keyword);
	    List<Restaurant> restaurantList = restaurantRepository.findAll();
	    List<User> userList = userRepository.findAll();
	    Page<Review> reviewList;
	    
	    if(keyword != null) {
	    	reviewList = reviewRepository.findByRestaurant(restaurant,pageable);
	    }else {
	    	reviewList = reviewRepository.findAll(pageable);
	    }
	   
	    model.addAttribute("restaurantList", restaurantList);
	    model.addAttribute("userList", userList);
	    model.addAttribute("reviewList", reviewList);
	    model.addAttribute("keyword", keyword);
	         
	         return "admin/review/index";
	}
	
	@GetMapping("/userSearch") //レビュー管理ページで、会員検索を実行した際の処理
    public String userSearch(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable
   		 ,@RequestParam(name = "userKeyword", required = false) String userSearch
   		 ,@RequestParam(name = "keyword", required = false) String keyword
   		 ,Model model) {
	User user = userRepository.findByEmail(userSearch);
   List<Restaurant> restaurantList = restaurantRepository.findAll();
   List<User> userList = userRepository.findAll();
   Page<Review> reviewList;
   
   if(userSearch != null) {
   	reviewList = reviewRepository.findByUser(user,pageable);
   }else {
   	reviewList = reviewRepository.findAll(pageable);
   }
  
   model.addAttribute("restaurantList", restaurantList);
   model.addAttribute("userList", userList);
   model.addAttribute("reviewList", reviewList);
   model.addAttribute("keyword", keyword);
        
        return "admin/review/index";
}
	
	//管理者ページの店舗一覧で、削除ボタンを押下後の処理を実装
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

			reviewService.deleteReview(id);
		
			// 成功した場合の処理
			redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");


		return "redirect:/admin/review";
	}
}
