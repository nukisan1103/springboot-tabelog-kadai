package com.example.nagoyamesi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyamesi.entity.User;

import lombok.Getter;

@Getter
public class PassResetEvent extends ApplicationEvent{
	private User user;
    private String requestUrl;   
    
    public PassResetEvent(Object source, User user, String requestUrl) {
        super(source);
        
        this.user = user;        
        this.requestUrl = requestUrl;
    }
   

}