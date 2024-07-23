package com.example.nagoyamesi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationRegisterForm;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}

	//予約情報格納メソッド
	@Transactional
	public void create(ReservationRegisterForm reservationRegisterForm) {
		Reservation reservation = new Reservation();
		Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
		LocalDate checkinDate = reservationRegisterForm.getReservationDate();

		reservation.setRestaurant(restaurant);
		reservation.setUser(user);
		reservation.setReservationDateTime(checkinDate);
		reservation.setReservationTime(reservationRegisterForm.getReservationTime());
		reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

		reservationRepository.save(reservation);
	}

	// 来店人数が定員以下かどうかをチェックする
	public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
		return numberOfPeople <= capacity;
	}
	
	//ユーザーが入力した日付が入力日よりも前かどうかチェック
	public boolean dateCheck(LocalDate mydate, LocalDate today) {
		
		return mydate.isAfter(today);

	}
	////ユーザーが入力した時刻が現時刻よりも前だったらfalse
	public boolean timeCheck(LocalTime mytime, LocalTime nowtime) {
		return mytime.isAfter(nowtime);

	}

	public boolean twoHoursLaterCheck(LocalTime mytime, LocalTime twoHoursLater) {
		//ユーザーが入力した日付が現在より二時間後以内だったらfalse
		return mytime.isAfter(twoHoursLater);

	}
	
	//ユーザーが入力した日付が、当日かどうかチェック
	public boolean isSameDate(LocalDate date1, LocalDate date2) {
		return date1 != null && date1.equals(date2);
	}

	//ユーザーが指定した予約時間に、満席かどうかを判定
	public boolean capaCheck(List<Reservation> timeSearch, int numberOfPeople, int capacity) {
		int capaCheck = numberOfPeople;
		for (int i = 0; i < timeSearch.size(); i++) {
			int capaOutCheck = timeSearch.get(i).getNumberOfPeople();
			capaCheck += capaOutCheck;
		}if(capaCheck>capacity) {
		return false;
		}
		return true;

	}

	//ユーザーが入力した時刻が店舗の営業時間前かを判定。
	public boolean isBeforeOpen(LocalTime openindTime, LocalTime mytime) {
		
		return openindTime.isBefore(mytime);
		
	}
	//ユーザーが入力した時刻が店舗の営業時間後かを判定。
	public boolean isAfterOpen(LocalTime closindTime, LocalTime mytime) {
		// TODO 自動生成されたメソッド・スタブ
		return closindTime.isAfter(mytime);
	}
}