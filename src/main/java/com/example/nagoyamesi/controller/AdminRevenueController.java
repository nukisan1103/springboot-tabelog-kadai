package com.example.nagoyamesi.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Role;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.RoleRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.security.UserDetailsImpl;

@Controller
@RequestMapping("/admin/revenue")
public class AdminRevenueController {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	//private final SubscriptionService subscriptionService;

	private static final int SUBSCRIPTION_FEE = 330;

	public AdminRevenueController(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		//this.subscriptionService = subscriptionService;
	}

	//管理者用売上管理ページ遷移用
	@GetMapping
	public String index(Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		Role role = roleRepository.findByName("ROLE_SUBSCRIBER");
		List<User> user = userRepository.findByRole(role);
		Map<String,Long> revenueInfo = new HashMap<>();

		LocalDate today = LocalDate.now();
		long period = 0;
		long totalRevenue = 0;
		//取得した有料会員データ分だけ繰り返す
		for (int i = 0; i < user.size(); i++) {
			long revenue = 0;
			if (user.get(i).getSubscriptionStartDate() != null) {
				LocalDate startDate = user.get(i).getSubscriptionStartDate();
				//有料会員になってからの期間から本日までの期間を算出
				period = ChronoUnit.MONTHS.between(startDate, today);
				//↑で算出した期間に月額料金を掛けて、個人のサブスク支払い料金の総額を取得
				revenue = period * SUBSCRIPTION_FEE;
				totalRevenue += revenue;
				revenueInfo.put(user.get(i).getName(), revenue);
			}
		}
		
		 model.addAttribute("totalRevenue", totalRevenue);
		 model.addAttribute("revenueInfo", revenueInfo);

		return "admin/revenue/index";
	}
	@GetMapping("/dateSearch") //管理者が指定した年月までの売上を算出する
	public String dateSearch(Model model, @RequestParam(name = "period", required = false) String selectPeriod) {

		Role role = roleRepository.findByName("ROLE_SUBSCRIBER");
		List<User> user = userRepository.findByRole(role);
		Map<String,Long> revenueInfo = new HashMap<>();
		

		 // "yyyy-MM"フォーマットのDateTimeFormatterを作成
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // "YYYY-MM-01"形式の文字列をLocalDateに変換
        LocalDate selectPeriodDate = LocalDate.parse(selectPeriod + "-01", formatter);
		LocalDate today = LocalDate.now();
		long period = 0;
		long totalRevenue = 0;
		//取得した有料会員データ分だけ繰り返す
		for (int i = 0; i < user.size(); i++) {
			long revenue = 0;
			if (user.get(i).getSubscriptionStartDate() != null) {
				LocalDate startDate = user.get(i).getSubscriptionStartDate();
				//有料会員になった日が、選択した日付よりも前だった場合実行
				if(startDate.isBefore(selectPeriodDate)) {
				
					//有料会員になってからの期間から指定した年月までの期間を算出
					period = ChronoUnit.MONTHS.between(startDate, selectPeriodDate);
					//↑で算出した期間に月額料金を掛けて、個人のサブスク支払い料金の総額を取得
					revenue = period * SUBSCRIPTION_FEE;
					totalRevenue += revenue;
					revenueInfo.put(user.get(i).getName(), revenue);
				}else {
					break;
				}			
			}
		}
		
		 model.addAttribute("totalRevenue", totalRevenue);
		 model.addAttribute("revenueInfo", revenueInfo);

		return "admin/revenue/index";
	}

}