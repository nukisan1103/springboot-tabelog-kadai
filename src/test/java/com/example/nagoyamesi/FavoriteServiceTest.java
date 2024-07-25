package com.example.nagoyamesi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.nagoyamesi.entity.Favorite;
import com.example.nagoyamesi.entity.Restaurant;
import com.example.nagoyamesi.repository.FavoriteRepository;
import com.example.nagoyamesi.repository.RestaurantRepository;
import com.example.nagoyamesi.service.FavoriteService;

@SpringBootTest
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    // テストメソッド
    @Test
    void testIsAlreadyRegist() {
        // テストデータ
        int userId = 1;
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setId(1);

        // モックの設定：ユーザーがレストランをお気に入り登録している場合
        Favorite mockFavorite = new Favorite();
        when(favoriteRepository.findByUserAndRestaurant(userId, testRestaurant.getId())).thenReturn(mockFavorite);

        // メソッドを実行して結果を確認
        boolean result = favoriteService.isAlreadyRegist(userId, testRestaurant);
        // 結果がtrueであることを確認
        assertTrue(result, "ユーザーがレストランをお気に入り登録している場合、trueを返すべき");

        // モックの設定
        when(favoriteRepository.findByUserAndRestaurant(userId, testRestaurant.getId())).thenReturn(null);

        // メソッドを実行して結果を確認
        result = favoriteService.isAlreadyRegist(userId, testRestaurant);
        // 結果がfalseであることを確認
        assertFalse(result, "ユーザーがレストランをお気に入り登録していない場合、falseを返すべき");
    }
}
