package com.loginAuth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.loginAuth.dto.UserDto;
import com.loginAuth.mapper.UserMapper;

@Service
public class UserService {

	@Autowired
	UserMapper userMapper;

	@Autowired
	EmailService emailService;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * Login authentication
	 */
	public UserDto findByEmailandPassword(String email, String rawPassword) {
		UserDto user = userMapper.findByEmail(email);
		if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
			return user;
		}
		return null;
	}

	/**
	 * User registration
	 */
	public boolean registerUser(UserDto user) {
		if (userMapper.findByEmail(user.getEmail()) != null) {
			return false; // email already exists
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userMapper.save(user);
		return true;
	}

	/**
	 * Send password reset link
	 */
	public void sendResetLink(String email) {
		UserDto user = userMapper.findByEmail(email);
		if (user == null)
			return;

		String token = UUID.randomUUID().toString();
		LocalDateTime expiry = LocalDateTime.now().plusMinutes(3);

		userMapper.saveResetToken(email, token, expiry);

		String link = "http://localhost:8080/reset-password?token=" + token;
		emailService.sendResetEmail(email, link);
	}

	/**
	 * Find user by reset token
	 */
	public UserDto findByToken(String token) {
		return userMapper.findByToken(token);
	}

	/**
	 * Reset password
	 */
	public boolean resetPassword(String token, String newPassword) {
		UserDto user = userMapper.findByToken(token);

		if (user == null) {
			return false;
		}

		String encoded = passwordEncoder.encode(newPassword);

		userMapper.updatePassword(user.getEmail(), encoded);

		// Clear token after successful reset
		userMapper.clearResetToken(user.getEmail());
		return true;
	}
}
