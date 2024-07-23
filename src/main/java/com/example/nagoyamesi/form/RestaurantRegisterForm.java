package com.example.nagoyamesi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRegisterForm {
    @NotBlank(message = "店舗名を入力してください。")
    private String name;
    

    private String category_name;   
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String restaurants_description;   
    
    @NotNull(message = "最低料金を入力してください。")
    @Min(value = 1, message = "最低料金は1円以上に設定してください。")
    private Integer lowest_price;  
    
    @NotNull(message = "最高料金を入力してください。")
    private Integer highest_price;  
    
    @NotBlank(message = "開店時間を入力してください。")
    private String opening_time;
    
    @NotBlank(message = "閉店時間を入力してください。")
    private String closing_time;
    
    @NotBlank(message = "定休日を入力してください。")
    private String regular_holiday;
    
    @NotNull(message = "定員を入力してください。")
    @Min(value = 1, message = "定員は1人以上に設定してください。")
    private Integer capacity;     
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "電話番号を入力してください。")
    private String phone_number;
}