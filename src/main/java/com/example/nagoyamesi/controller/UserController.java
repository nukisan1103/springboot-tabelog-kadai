
package com.example.nagoyamesi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.UserEditForm;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;
import com.example.nagoyamesi.service.StripeService;
import com.example.nagoyamesi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;    
    private final UserService userService; 
    private final StripeService stripeService; 
    
    public UserController(UserRepository userRepository, UserService userService
    		, StripeService stripeService) {
        this.userRepository = userRepository;  
        this.userService = userService;
        this.stripeService = stripeService;
    }    
    
    @GetMapping //会員情報ページ遷移用
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        
        model.addAttribute("user", user);
        
        return "user/index";
    }
    
    @GetMapping("/edit") //会員情報編集ページ用
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getKana(), user.getAddress(), user.getPhone_number(), user.getEmail());
        
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit";
    }    
    @PostMapping("/update") //会員情報編集実行用
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult
    		, RedirectAttributes redirectAttributes) {
        // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }
        
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        
        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
        return "redirect:/user";
    }    
    @GetMapping("/upgrade") //無料会員を有料会員にアップグレードする
    public String upgrade(Model model,HttpServletRequest httpServletRequest) {         
       //Stripe接続用にsessionIdを取得
    	String sessionId = stripeService.createStripeSession(httpServletRequest);
    	
   	 model.addAttribute("sessionId", sessionId);
   	 return "user/confirm";
    }
    
    @GetMapping("/upgradeExecution") //無料会員を有料会員へアップグレード実行
    public String upgradeExecution(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    		,RedirectAttributes redirectAttributes) {         
    	
    	User user = userDetailsImpl.getUser();
    	
    	userService.upgrade(user);
    	
    	redirectAttributes.addFlashAttribute("successMessage", "有料会員にアップグレードしました。有料会員としてログインする場合は一旦ログアウトしてください。");
    	  
    	 return "redirect:/login";
    	     
    }
}
