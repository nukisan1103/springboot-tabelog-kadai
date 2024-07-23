package com.example.nagoyamesi.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //@AllArgsConstructorアノテーションをつけることで、全てのフィールドを引数に受け取るコンストラクタを自動生成できる。
public class ReviewEditForm {
	
	 @NotNull(message = "スコアを入力してください。")
	    private Integer score;

	    @NotBlank(message = "レビュー内容を入力してください。")
	    @Length(max = 300, message = "レビューは300文字以内で入力してください。")
	    private String sentense;

}