package com.example.nagoyamesi.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	
	@Transactional
	public void deleteByRestaurant(Restaurant restaurant);

	public List<Reservation> findByRestaurant(Restaurant restaurant);
	

	public Reservation findByUserAndRestaurant(User user, Restaurant restaurant);

	public List<Reservation> findByRestaurantAndReservationDateTimeAndReservationTime(Restaurant restaurant,
			LocalDate mydate, LocalTime mytime);

	public void deleteByUser(User user);
	

	}