package com.example.nagoyamesi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer id;
    
    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "name")
    private String name;

    @Column(name = "image_name")
    private String image_name;

    @Column(name = "restaurant_description")
    private String description;

    @Column(name = "lowest_price")
    private Integer lowest_price;

    @Column(name = "highest_price")
    private Integer highest_price;

    @Column(name = "opening_time")
    private String opening_time;

    @Column(name = "closing_time")
    private String closing_time;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "address")
    private String address;

    @Column(name = "regular_holiday")
    private String regular_holiday;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}