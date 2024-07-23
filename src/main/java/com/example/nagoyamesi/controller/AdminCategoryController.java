package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Category;
import com.example.nagoyamesi.form.CategoryRegisterForm;
import com.example.nagoyamesi.repository.CategoryRepository;
import com.example.nagoyamesi.service.CategoryService;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;

	public AdminCategoryController(CategoryRepository categoryRepository
			,CategoryService categoryService) {

		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;

	}
	
	//管理者用カテゴリ一覧ページへ
	@GetMapping("/index")
	public String index(Model model) {

		//カテゴリーを全権取得
		List<Category> categories = categoryRepository.findAll();

		model.addAttribute("categories", categories);

		return "admin/category/index";
	}
	
	//管理者用カテゴリ削除
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") int id, RedirectAttributes redirectAttributes, Model model) {

			categoryService.deleteCategory(id);
		
			// 成功した場合の処理
			redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");


		return "redirect:/admin/category/index";
	}
	
	//管理者用：店舗一覧のカテゴリー新規登録ボタン押下後の処理。
	@GetMapping("/register")
	public String categoryRegister(Model model) {

		model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
		return "admin/category/register";
	}
	
	//カテゴリー登録処理実行。実行後は店舗一覧へリダイレクトし、成功メッセージ表示。
		@PostMapping("/create")
		public String categoryCreate(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
				BindingResult bindingResult, RedirectAttributes redirectAttributes) {

			String getCategory = categoryRegisterForm.getCategory_name();
			Category categoryCheck = categoryRepository.findByCategory(getCategory);

			if (categoryCheck != null) { //入力されたカテゴリ名が既存だった場合はエラーを返す
				FieldError fieldError = new FieldError(bindingResult.getObjectName(), "category_name",
						"そのカテゴリーは既に登録されています。");
				bindingResult.addError(fieldError);
			}
			if (bindingResult.hasErrors()) {
				return "admin/category/register";
			}

			categoryService.create(categoryRegisterForm);
			redirectAttributes.addFlashAttribute("successMessage", "カテゴリーを登録しました。");

			return "redirect:/admin/restaurants";
		}
		
		//管理者用：カテゴリー検索結果を返す
		@GetMapping("/search")
		public String categorySearch(Model model,
				@RequestParam(name = "keyword", required = false) String keyword) {
			
			List<Category> categoryList;
			
			if (keyword != null && !keyword.isEmpty()) {
				
			categoryList = categoryRepository.findByCategoryLike("%" + keyword + "%");
			
			}else {
				
			categoryList = categoryRepository.findAll();
			
			}
			model.addAttribute("categories", categoryList);
			return "admin/category/index";
		}
}