package com.example.nagoyamesi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.entity.VerificationToken;
import com.example.nagoyamesi.repository.VerificationTokenRepository;

@Service
public class PassResetTokenService {

	private final VerificationTokenRepository verificationTokenRepository;

	public PassResetTokenService(VerificationTokenRepository verificationTokenRepository) {
	
		this.verificationTokenRepository = verificationTokenRepository;

	}

	@Transactional
	public void create(User user, String token) {
		//パスワードリセット希望のユーザーの情報を取得
		VerificationToken userSearch = verificationTokenRepository.findByUser(user);
		//取得したユーザー情報のトークンを上書き
		userSearch.setToken(token);
		verificationTokenRepository.save(userSearch);
	}

	// トークンの文字列で検索した結果を返す
	public VerificationToken getPassResetToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}
}