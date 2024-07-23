package com.example.nagoyamesi.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Role;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.SignupForm;
import com.example.nagoyamesi.form.UserEditForm;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.repository.ReviewRepository;
import com.example.nagoyamesi.repository.RoleRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.repository.VerificationTokenRepository;


@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationTokenRepository verificationTokenRepository;
	private final ReviewRepository reviewRepository;
	private final ReservationRepository reservationRepository;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder
			,VerificationTokenRepository verificationTokenRepository,ReviewRepository reviewRepository
			,ReservationRepository reservationRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.verificationTokenRepository = verificationTokenRepository;
		this.reviewRepository = reviewRepository;
		this.reservationRepository = reservationRepository;
	}
	//会員登録用 登録時は無料会員となる
	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");

		user.setName(signupForm.getName());
		user.setKana(signupForm.getKana());
		user.setAddress(signupForm.getAddress());
		user.setPhone_number(signupForm.getPhone_number());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);

		return userRepository.save(user);
	}
	
	//会員情報編集用
	@Transactional
	public void update(UserEditForm userEditForm) {
		//getReferenceById関数で、編集対象のユーザーのIDを取得する
		User user = userRepository.getReferenceById(userEditForm.getId());

		user.setName(userEditForm.getName());
		user.setKana(userEditForm.getKana());
		user.setAddress(userEditForm.getAddress());
		user.setPhone_number(userEditForm.getPhone_number());
		user.setEmail(userEditForm.getEmail());

		userRepository.save(user);
	}

	//無料会員を有料会員にアップグレード
	@Transactional
	public void upgrade(User upgradeUser) {

		User user = userRepository.getReferenceById(upgradeUser.getId());
		Role role = roleRepository.findByName("ROLE_SUBSCRIBER");

		user.setRole(role);
		user.setSubscriptionStartDate(LocalDate.now());//有料会員登録時の日付を記録する。売上管理用フィールド

	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	@Transactional //ハッシュ化したパスワードをセットする。
	public void passReset(String password, Integer id) {
		User user = userRepository.getReferenceById(id);

		user.setPassword(passwordEncoder.encode(password));

		userRepository.save(user);
	}
	//会員退会用 退会する会員の全てのデータを削除する
	@Transactional
	public void withdrawal(User user) {
		
		verificationTokenRepository.deleteByUser(user);
		reviewRepository.deleteByUser(user);
		reservationRepository.deleteByUser(user);
		userRepository.deleteById(user.getId());
		
	}

}