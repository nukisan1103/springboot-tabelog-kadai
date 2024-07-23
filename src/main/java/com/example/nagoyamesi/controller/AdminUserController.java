
package com.example.nagoyamesi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Role;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.RoleRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserService userService; 

	public AdminUserController(UserRepository userRepository, RoleRepository roleRepository
			,UserService userService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userService = userService;
	}
	
	//管理者用会員一覧ページ遷移用
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<User> userPage;

		if (keyword != null && !keyword.isEmpty()) {
			userPage = userRepository.findByNameLikeOrKanaLike("%" + keyword + "%", "%" + keyword + "%", pageable);
		} else {
			userPage = userRepository.findAll(pageable);
		}

		model.addAttribute("userPage", userPage);
		model.addAttribute("keyword", keyword);

		return "admin/users/index";
	}
	
	//管理者用会員詳細ページ遷移
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		User user = userRepository.getReferenceById(id);

		model.addAttribute("user", user);

		return "admin/users/show";
	}
	@PostMapping("/{id}/delete") // 管理者：会員削除用
	public String userDelete(@PathVariable(name = "id") Integer id, Model model
			,RedirectAttributes redirectAttributes) {
		
		User user = userRepository.getReferenceById(id);
		
		userService.withdrawal(user);
		
		redirectAttributes.addFlashAttribute("successMessage", "会員を削除しました。");
		
		return "redirect:/admin/users";
	}
	
	//管理者用会員一覧ページでロール検索実行事の処理
	@GetMapping("/roleresearch")
	public String roleSearch(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 20, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		
		Page<User> userPage;
		
		Role role = roleRepository.findByName(keyword);
		userPage = userRepository.findByRole(role, pageable);
		

		model.addAttribute("userPage", userPage);

		return "admin/users/index";
	}

}
