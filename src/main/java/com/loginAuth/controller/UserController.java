package com.loginAuth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.loginAuth.dto.UserDto;
import com.loginAuth.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * Display the login page.
	 */
	@GetMapping("/login")
	public String login() {

		return "login";
	}

	/**
	 * Process login request. Validates email and password and stores the email in
	 * session on success.
	 */
	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {

		UserDto user = userService.findByEmailandPassword(email, password);

		// If authentication fails, return to login page with error message
		if (user == null) {
			model.addAttribute("error", "Invalid email or password.");
			return "login";
		}

		// Store logged-in user's email in the session
		session.setAttribute("email", email);

		// Redirect to home page after successful login
		return "redirect:/home";
	}

	/**
	 * Log out the user by invalidating the session.
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	/**
	 * Display the home page. Redirects to login page if the user is not logged in.
	 */
	@GetMapping("/home")
	public String homePage(Model model, @AuthenticationPrincipal OAuth2User oauthUser, HttpSession session) {
		String email = (String) session.getAttribute("email");

		if (oauthUser != null) {
			email = oauthUser.getAttribute("email");
			session.setAttribute("email", email);
		}

		// If no user session exists, redirect to login page
		if (email == null) {
			return "redirect:/login";
		}

		// Pass logged-in user's email to the view
		model.addAttribute("email", email);
		return "home";
	}

	/**
	 * Display the registration page.
	 */
	@GetMapping("/register")
	public String showRegisterForm() {
		return "register";
	}

	/**
	 * Process user registration. Registers a new user if the email does not already
	 * exist.
	 */
	@PostMapping("/register")
	public String registerUser(@RequestParam String email, @RequestParam String password,
			@RequestParam String confirmPassword, Model model, RedirectAttributes redirectAttributes) {

		UserDto user = new UserDto();
		user.setEmail(email);
		user.setPassword(password);

		// Attempt to register user
		boolean success = userService.registerUser(user);

		// If email already exists, return to registration page with error
		if (!success) {
			model.addAttribute("error", "Email already exists");
			return "register";
		}

		// Redirect to login page with success message
		redirectAttributes.addFlashAttribute("message", "Registration successful! You can log in now.");
		return "redirect:/login";
	}

	/**
	 * Display the forgot password page.
	 */
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}

	/**
	 * Process forgot password request. Sends a password reset link if the email
	 * exists.
	 */
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, Model model) {

		userService.sendResetLink(email);
		model.addAttribute("message", "If the email address exists, a password reset link has been sent.");
		return "forgot-password";
	}

	/**
	 * Display the reset password page. Validates the reset token before showing the
	 * form.
	 */
	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam String token, Model model, RedirectAttributes redirectAttributes) {
		UserDto user = userService.findByToken(token);

		// If token is invalid or expired, redirect to login page
		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "The token is invalid or has expired.");
			return "redirect:/login";
		}

		// Pass token to reset password form
		model.addAttribute("token", token);
		return "reset-password";
	}

	/**
	 * Process password reset request. Validates token and updates the user's
	 * password.
	 */
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password,
			@RequestParam String confirmPassword, Model model, RedirectAttributes redirectAttributes) {

		boolean success = userService.resetPassword(token, password);

		// If token is invalid or expired, redirect to login page
		if (!success) {
			redirectAttributes.addFlashAttribute("error", "The token is invalid or has expired.");
			return "redirect:/login";
		}

		// Redirect to login page with success message
		redirectAttributes.addFlashAttribute("message",
				"Password reset successful. Please log in with your new password.");
		return "redirect:/login";
	}
}
