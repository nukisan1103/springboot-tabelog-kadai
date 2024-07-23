
package com.example.nagoyamesi.controller;


import java.net.URI;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;
import com.example.nagoyamesi.service.FavoriteService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping(value = {"/restaurants/subscriber/favorite"})
public class FavoriteController {
	
 private final RestaurantRepository restaurantRepository;
 private final FavoriteService favoriteService;

     public FavoriteController(RestaurantRepository restaurantRepository,FavoriteService favoriteService) {        
         
         this.restaurantRepository = restaurantRepository;
         this.favoriteService = favoriteService;
       
     }    

     @GetMapping("/index")//有料会員用お気に入り店舗一覧ページへ遷移
     public String getFavoriteRestaurants(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,Model model) {
    	 int userData = userDetailsImpl.getUser().getId();   
      List<Restaurant> favoriteRestaurantList =  favoriteService.getFavoriteRestaurantsByUserId(userData);
      
      model.addAttribute("favoriteRestaurantList", favoriteRestaurantList);
      
         return "favorite/index";
                 
     }
     
     //お気に入りに追加ボタン押下で、お気に入り登録し、完了後店舗詳細へ戻る
     @GetMapping("/{id}/regist")
     public String favoriteRegist(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		 @PathVariable(name = "id") Integer id, Model model
    		 ,RedirectAttributes redirectAttributes,UriComponentsBuilder builder) {
    	 
    	 int userId = userDetailsImpl.getUser().getId();    	 
    	 Restaurant restaurants = restaurantRepository.getReferenceById(id);
    	 //リダイレクト先のURLにリクエストパラメータをつけるため、UriComponentsBuilderクラスを使用
    	 URI location = builder.path("/restaurants/subscriber/" + restaurants.getId()).build().toUri();
    	 
    	 //既に自分が対象店舗の置き入り登録をしているかをチェックし、していればお気に入り登録し、してなければエラー表示
    	 if(!favoriteService.isAlreadyRegist(userId,restaurants)) {
    		 favoriteService.favoriteRegist(userId,restaurants.getId());
        	 redirectAttributes.addFlashAttribute("successMessage", "この店舗をお気に入りに追加しました。");
        	
    	 }else {
    		 redirectAttributes.addFlashAttribute("errorMessage", "この店舗は既にお気に入り登録済みです。");
    	 }
    	 return "redirect:" + location.toString();
                 
     }
     
    @PostMapping("/{id}/delete")
 	@Transactional
 	public String favoriteDelete(@PathVariable(name = "id") Integer id,
 			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,RedirectAttributes redirectAttributes) {
    	 int userId = userDetailsImpl.getUser().getId();    	 
 		favoriteService.favoriteDelete(id,userId);
 	  
 		redirectAttributes.addFlashAttribute("successMessage", "お気に入りを削除しました。");

 		return "redirect:/restaurants/subscriber/favorite/index";
     } 
}
