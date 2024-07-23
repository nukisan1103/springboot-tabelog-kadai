
package com.example.nagoyamesi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.entity.VerificationToken;
import com.example.nagoyamesi.event.PassResetEventPublisher;
import com.example.nagoyamesi.event.SignupEventPublisher;
import com.example.nagoyamesi.form.PasswordResetForm;
import com.example.nagoyamesi.form.PasswordResetInputForm;
import com.example.nagoyamesi.form.SignupForm;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.repository.VerificationTokenRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;
import com.example.nagoyamesi.service.PassResetTokenService;
import com.example.nagoyamesi.service.UserService;
import com.example.nagoyamesi.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
 private final UserService userService;
 private final SignupEventPublisher signupEventPublisher;
 private final VerificationTokenService verificationTokenService;
 private final PassResetEventPublisher passResetEventPublisher;
 private final UserRepository userRepository;
 private final PassResetTokenService passResetTokenService;
 private final VerificationTokenRepository verificationTokenRepository;

 public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService
		 ,PassResetEventPublisher passResetEventPublisher,UserRepository userRepository,PassResetTokenService passResetTokenService
		 ,VerificationTokenRepository verificationTokenRepository) { 
     this.userService = userService; 
     this.signupEventPublisher = signupEventPublisher;
      this.verificationTokenService = verificationTokenService;
      this.passResetEventPublisher = passResetEventPublisher;
      this.userRepository = userRepository;
      this.passResetTokenService = passResetTokenService;
      this.verificationTokenRepository = verificationTokenRepository;
 }    
    //ログイン画面遷移用
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    //無料会員登録画面遷移用
    @GetMapping("/signup")
    public String signup(Model model) {        
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }    
    
    //無料会員登録画面で登録ボタン押下後の処理
    @PostMapping("/signup")
     public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }    

        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        User createdUser = userService.create(signupForm);
        String requestUrl = new String(httpServletRequest.getRequestURL());
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。"); 

        return "redirect:/";
    }
    
    //無料会員登録確認画面で、登録ボタン押下後の処理
    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "auth/verify";         
    }    
    
    
    @GetMapping("/passwordChange")
    public String passwordChange(Model model) {        
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "auth/passResetting";
    }    
    
    //入力されたアドレスが既存かどうかをチェックし、既存であればイベント発行〜メール送信
    @PostMapping("/passwordChange")
    public String passwordChange(@ModelAttribute @Validated  PasswordResetForm  passwordResetForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
       // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
       if (!userService.isEmailRegistered(passwordResetForm.getEmail())) {
           FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "登録されていないメールアドレスです。");
           bindingResult.addError(fieldError);                       
       }    

       if (bindingResult.hasErrors()) {
           return "auth/passResetting";
       }

       User user = userRepository.findByEmail(passwordResetForm.getEmail());
       String requestUrl = new String(httpServletRequest.getRequestURL());
       passResetEventPublisher.publishPassResetEvent(user, requestUrl);
       redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、パスワードリセットを完了してください。"); 

       return "redirect:/";
   }
    //ユーザーに送信したURLに貼られたトークンと、verificationTokenエンティティに保存されたトークを比較し、
    //一致していればパスワードリセット用フォームを送信
    @GetMapping("/passwordChange/passreset")
    public String passReset(@RequestParam(name = "token") String token, Model model) {
    	//PassResetToken passResetToken = passResetTokenService.getPassResetToken(token);
    	VerificationToken passResetToken = passResetTokenService.getPassResetToken(token);
        if (passResetToken != null) {
        	VerificationToken userSearch = verificationTokenRepository.findByToken(token);
        	User user = userSearch.getUser();
        	model.addAttribute("passwordResetInputForm", new PasswordResetInputForm());
        	model.addAttribute("userInfo", user);
                   
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
            return "/";         
        }
        
        return "auth/passChanging";  
    }    
    
    @PostMapping("/passwordChange/{id}")
    public String passResetComplete(Model model,@PathVariable(name = "id") Integer id,
    		@ModelAttribute @Validated  PasswordResetInputForm  passwordResetInputForm, BindingResult bindingResult, 
    		RedirectAttributes redirectAttributes) {
       
      
    	//パスワードリセット画面で2回入力したパスワードが一致していなければエラー表示
       if (!userService.isSamePassword(passwordResetInputForm.getPassword(), passwordResetInputForm.getPasswordConfirmation())) {
           FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
           bindingResult.addError(fieldError);
       }
       if (bindingResult.hasErrors()) {
    	   User userInfo = userRepository.getReferenceById(id);
    	   //passChanging.htmlでPathVariable用にUser情報を送信しているため送り返す
    	   model.addAttribute("userInfo", userInfo);
    	   return "auth/passChanging";  
       }
       //リセット希望のユーザーのパスワードを更新する
       userService.passReset(passwordResetInputForm.getPassword(),id);
      
       redirectAttributes.addFlashAttribute("successMessage", "パスワードリセットが完了しました。");


       return "redirect:/";
   }
    
    @GetMapping("/withdrawal")
    public String withdrawal() {        
       
        return "auth/withdrawal";
    }  
    
    
    @GetMapping("/withdrawal/complete")
    public String withdrawalComplete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl
    		,RedirectAttributes redirectAttributes) {        
    	
    	User user = userDetailsImpl.getUser();
    	userService.withdrawal(user);
    	
    	redirectAttributes.addFlashAttribute("successMessage", "退会完了しました。再度会員登録する場合は、一旦ログアウトしてください。");
       
        return "redirect:/login";
    }  
}
