package com.example.nagoyamesi.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.nagoyamesi.entity.User;

public class SubscriptionService {

    private static final int SUBSCRIPTION_FEE = 330;

    public long calculateSubscriptionRevenue(User user) {
        if (user.getSubscriptionStartDate() == null) {
            return 0;
        }

        LocalDate startDate = user.getSubscriptionStartDate();
        LocalDate today = LocalDate.now();
        long months = ChronoUnit.MONTHS.between(startDate, today);

        return months * SUBSCRIPTION_FEE;
    }
}