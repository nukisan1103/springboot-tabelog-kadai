
package com.example.nagoyamesi.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class StripeService {
	@Value("${stripe.api-key}")
    private String stripeApiKey;
	
    // セッションを作成し、Stripeに必要な情報を返す
    public String createStripeSession(HttpServletRequest httpServletRequest) {
    	 Stripe.apiKey = stripeApiKey;
         
         String requestUrl = new String(httpServletRequest.getRequestURL());
         String priceId = "price_1Pa6d6P4bt7o7owHK1snJL2k";  
         
         SessionCreateParams params = new SessionCreateParams.Builder()
             .setSuccessUrl(requestUrl.replaceAll("/user/upgrade", "") + "/user/upgradeExecution")
             .setCancelUrl(requestUrl.replace("/user/confirm", ""))
             .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
             .addLineItem(new SessionCreateParams.LineItem.Builder()
                 .setQuantity(1L)
                 .setPrice(priceId)
                 .build()
             )
             .build();

         try {
             Session session = Session.create(params);
             return session.getId();
         } catch (StripeException e) {
             e.printStackTrace();
             return "";
         }
     }
 }
