
package com.example.nagoyamesi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReviewEditForm;
import com.example.nagoyamesi.form.ReviewForm;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.repository.ReviewRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;
import com.example.nagoyamesi.service.ReviewService;

@Controller
public class ReviewController {
	
 private final ReviewRepository reviewRepository;      
 private final ReviewService reviewService;
 private final UserRepository userRepository;
 private final RestaurantRepository restaurantRepository;

     
     public ReviewController(ReviewRepository reviewRepository,ReviewService reviewService,UserRepository userRepository
    		 ,RestaurantRepository restaurantRepository) {        
         this.reviewRepository = reviewRepository;        
         this.reviewService = reviewService;
         this.userRepository = userRepository;
         this.restaurantRepository = restaurantRepository;
        
     }    
     //レビュー一覧ページへ
     @GetMapping("/review")
     public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
         User user = userDetailsImpl.getUser();
         Page<Review> reviewPage = reviewRepository.findByUserOrderByCreatedAtDesc(user, pageable);
         
         model.addAttribute("reviewPage", reviewPage);         
         
         return "review/index";
     }
     
     //レビュー投稿ページへ
     @GetMapping("/{id}/review")
     public String postReview(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 @PathVariable(name = "id") Integer id,Model model,RedirectAttributes redirectAttributes) {
    	 User user = userDetailsImpl.getUser();
    	 Restaurant restaurants = restaurantRepository.getReferenceById(id);
    	 //ログイン中のユーザーが、レビュー投稿しようとしている店舗に、過去投稿していないかをチェック
    	 Review review = reviewRepository.findByUserAndRestaurant(user, restaurants);
    	 
    	 if(review != null) {
    		 redirectAttributes.addFlashAttribute("errorMessage", "レビューはひとつの店舗に1回までです。再度投稿する場合は、前回のレビューを削除、または編集してください。");
    		 return "redirect:/review";
    	 }
    	 
    	 model.addAttribute("restaurants",restaurants);
    	 model.addAttribute("reviewForm", new ReviewForm());
         return "review/post";
    	 
     }
     
     //全ユーザーレビュー投稿一覧へ
     @GetMapping("/{id}/review/show")
     public String showReview(@PathVariable(name = "id") Integer id,Model model
    		 ,@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
    	 
    	 Page<Review> reviewPage;
    	 Restaurant restaurants = restaurantRepository.getReferenceById(id);
    	 
    	 reviewPage = reviewRepository.findByRestaurant(restaurants, pageable);
    	 
    	 model.addAttribute("reviewPage",reviewPage);
    	    	
         return "review/show";
    	 
     }
     
     //レビュー登録完了
     @PostMapping("/{id}/review/create")
     public String reviewCreate(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 @ModelAttribute @Validated ReviewForm reviewForm,RedirectAttributes redirectAttributes,BindingResult bindingResult
    		 ,@PathVariable(name = "id") Integer id) {
    	     	 
    	 if (bindingResult.hasErrors()) {
             return "/subscriber/restautants/index";
         }
    	 
    	 User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId()); 
    	 Restaurant restaurants = restaurantRepository.getReferenceById(id);
    	 
    	 reviewService.create(user,restaurants,reviewForm);
    	 redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");   
    	 
    	 return "redirect:/restaurants/subscriber";
     }
     
   //レビュー編集用
     @GetMapping("/{id}/review/delete")
     public String reviewDelete(@PathVariable(name = "id") Integer id,Model model
    		 ,RedirectAttributes redirectAttributes) {
    	 
    	 reviewService.deleteReview(id);
    	 
    	 redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");   
  	 
    	 return "redirect:/review";
    	 
     }
     
     //レビュー編集用
     @GetMapping("/{id}/review/edit")
     public String reviewEdit(@PathVariable(name = "id") Integer id,Model model) {
    	 
    	 Review review = reviewRepository.getReferenceById(id);
    	 ReviewEditForm reviewEditForm  = new  ReviewEditForm(review.getScore(),review.getSentense());
    	 
    	 model.addAttribute("reviewEditForm",reviewEditForm);
    	 model.addAttribute("reviewId",review);
    	 
    	 return "review/edit";
    	 
     }
     
     //レビュー編集登録用
     @PostMapping("/{id}/review/update")
     public String reviewUpdate(@PathVariable(name = "id") Integer id,Model model,
    		 @ModelAttribute @Validated ReviewEditForm reviewEditForm,
    		 RedirectAttributes redirectAttributes) {
    	 
    	 reviewService.update(id,reviewEditForm);
    	 redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました。");   
    	 
    	 return "redirect:/review";
    	 
     }

}
