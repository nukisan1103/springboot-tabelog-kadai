
package com.example.nagoyamesi.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.example.nagoyamesi.form.ReservationRegisterForm;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;
import com.example.nagoyamesi.service.ReservationService;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final ReservationService reservationService;

	public ReservationController(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
			ReservationService reservationService) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.reservationService = reservationService;
	}

	@GetMapping("/reservations") //会員の予約一覧ページへ遷移
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute("reservationPage", reservationPage);

		return "reservation/index";
	}

	//店舗詳細画面から予約管理画面へ遷移。その時、予約重複が内科の確認するロジックも作成
	@GetMapping("/restaurants/{id}/reservations/input")
	public String input(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable(name = "id") Integer id,
			@ModelAttribute @Validated ReservationInputForm reservationInputForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model, Pageable pageable) {

		//予約対象の店舗データを取得
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		//予約フォームに入力した予約日の取得
		LocalDate mydate = reservationInputForm.getReservationDate();
		//予約フォームに入力した予約時間の取得
		LocalTime mytime = reservationInputForm.getReservationTime();
		//現在日を取得
		LocalDate nowdate = LocalDate.now();
		//現在の時間を取得
		LocalTime nowtime = LocalTime.now();
		//自分が指定した予約人数
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
		//予約希望の店舗の定員数を取得
		Integer capacity = restaurant.getCapacity();

		//ログインユーザーの情報取得		
		User user = userDetailsImpl.getUser();
		LocalTime openindTime = LocalTime.parse(restaurant.getOpening_time());
		LocalTime closindTime = LocalTime.parse(restaurant.getClosing_time());
		
	
		//----------自分が既に希望店舗に予約しているかどうかを確認---------------
		Reservation alreadyCheck = reservationRepository.findByUserAndRestaurant(user,restaurant);
	
		if (alreadyCheck != null && reservationService.isSameDate(mydate, alreadyCheck.getReservationDateTime())) {
		    FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationDate",
		            "あなたは既に当日予約されております。");
		    bindingResult.addError(fieldError);
		}

			if (bindingResult.hasErrors()) {
				model.addAttribute("restaurants", restaurant);
				model.addAttribute("errorMessage", "予約内容に不備があります。");
				return "subscriber/restaurants/show";
			}
		//-----------------------------------------------------------------
		
		
		//----自分の予約希望時点に、既に定員人数を来店人数が超えていたらエラー表示-----	
		List<Reservation> timeSearch = 
				reservationRepository.findByRestaurantAndReservationDateTimeAndReservationTime(restaurant,mydate,mytime);
		reservationService.capaCheck(timeSearch,numberOfPeople,capacity);
		
		
			if(!reservationService.capaCheck(timeSearch,numberOfPeople,capacity)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationTime",
						"その時間は満席、もしくは定員超えで現在予約できません。");
				bindingResult.addError(fieldError);
			}
			if (bindingResult.hasErrors()) {
				model.addAttribute("restaurants", restaurant);
				model.addAttribute("errorMessage", "予約内容に不備があります。");
				return "subscriber/restaurants/show";
			}		
		//-----------------------------------------------------------------


		//------------------------------入力フォームチェック用------------------------------------
		//店舗の定員を超えた場合はエラー文を返す。
		if (numberOfPeople != null) {
			if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople",
						"来店人数が定員を超えています。");
				bindingResult.addError(fieldError);
			}
			//予約希望日が過去日だった場合はエラー文を返す。
			if (mydate != null && !(mydate.equals(nowdate))) {
				if (!reservationService.dateCheck(mydate, nowdate)) {
					FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationDate",
							"過去の日付は選択出来ません。");
					bindingResult.addError(fieldError);
				}
			}
			//予約希望時間が過去日であればそれぞれエラー文を返す。
			if (mytime != null && !(mytime.equals(nowtime))) {
				if (!reservationService.timeCheck(mytime, nowtime) && !reservationService.dateCheck(mydate, nowdate)) {
					FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationTime",
							"過去の時間は選択出来ません。");
					bindingResult.addError(fieldError);
				}
			if (!(mytime.equals(openindTime)) && !reservationService.isBeforeOpen(openindTime,mytime)) {
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationTime",
						"営業時間外です。");
				bindingResult.addError(fieldError);
				}if(!(mytime.equals(openindTime)) && !reservationService.isAfterOpen(closindTime,mytime)) {
					FieldError fieldError = new FieldError(bindingResult.getObjectName(), "reservationTime",
							"営業時間外です。");
					bindingResult.addError(fieldError);
				}
				
			}
		}
		
		if (bindingResult.hasErrors()) { //bindingResultにエラーが格納されていた場合はエラー文を返す
			model.addAttribute("restaurants", restaurant);
			model.addAttribute("errorMessage", "予約内容に不備があります。");
			return "subscriber/restaurants/show";
		}

		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);

		return "redirect:/restaurants/{id}/reservations/confirm";
	}

	@GetMapping("/restaurants/{id}/reservations/confirm") //予約確認ページ遷移用
	public String confirm(@PathVariable(name = "id") Integer id,
			@ModelAttribute ReservationInputForm reservationInputForm,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		User user = userDetailsImpl.getUser();
		
		//予約確認用に、登録フォーム（reservationInputForm）に入力した内容を詰め替える
		ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(restaurant.getId(), user.getId(),
				reservationInputForm.getReservationDate(), reservationInputForm.getReservationTime(),
				reservationInputForm.getNumberOfPeople());

		model.addAttribute("restaurants", restaurant);
		model.addAttribute("reservationRegisterForm", reservationRegisterForm);

		return "reservation/confirm";
	}

	@PostMapping("/restaurants/{id}/reservations/create") //予約完了処理
	public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {
		reservationService.create(reservationRegisterForm);

		return "redirect:/reservations?reserved";
	}
	
	@PostMapping("/reservation/{id}/cancel") //予約キャンセル処理実施
	public String reservationCancel(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		
		reservationRepository.deleteById(id);
			
		redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");
		return "redirect:/reservations";
	}
	
}
