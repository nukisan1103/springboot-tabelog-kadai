package com.example.nagoyamesi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyamesi.entity.User;


@Component
public class PassResetEventPublisher {
	
	 private final ApplicationEventPublisher applicationEventPublisher;
     
     public PassResetEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
         this.applicationEventPublisher = applicationEventPublisher;                
     }
     
	public void publishPassResetEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new PassResetEvent(this, user, requestUrl));
		
	}
	

}