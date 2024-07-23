package com.example.nagoyamesi.form;



import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotNull(message = "予約日を選択してください。")
	private LocalDate reservationDate;
	
	@NotNull(message = "予約時間を選択してください。")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime reservationTime;
	

	@NotNull(message = "人数を入力してください。")
	@Min(value = 1, message = "人数は1人以上に設定してください。")
	private Integer numberOfPeople;

}