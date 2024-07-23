package com.example.nagoyamesi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.service.PassResetTokenService;

@Component
public class PassResetEventListener {
	
	 
	 private final PassResetTokenService passResetTokenService; 
	    private final JavaMailSender javaMailSender;
	    
	    public PassResetEventListener(JavaMailSender mailSender,PassResetTokenService passResetTokenService) {
	    		
	             
	        this.javaMailSender = mailSender;
	        this.passResetTokenService = passResetTokenService;
	    }

	    @EventListener
	    private void onSignupEvent(PassResetEvent passResetEvent) {
	        
	        User userMail = passResetEvent.getUser();
	        String token = UUID.randomUUID().toString();
	        passResetTokenService.create(userMail, token);
	        
	        String recipientAddress = userMail.getEmail();
	        String subject = "メール認証";
	        String confirmationUrl = passResetEvent.getRequestUrl() + "/passreset?token=" + token;
	        String message = "以下のリンクをクリックしてパスワードリセットを完了してください。";
	       
	        SimpleMailMessage mailMessage = new SimpleMailMessage(); 
	        mailMessage.setTo(recipientAddress);
	        mailMessage.setSubject(subject);
	        mailMessage.setText(message + "\n" + confirmationUrl);
	        javaMailSender.send(mailMessage);
}
}