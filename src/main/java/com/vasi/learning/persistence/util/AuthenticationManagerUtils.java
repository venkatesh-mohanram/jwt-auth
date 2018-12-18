package com.vasi.learning.persistence.util;

import com.vasi.learning.model.v1.User;

public class AuthenticationManagerUtils {
	private final static String SECRET = "841D8A6C80CBA4FCAD32D5367C18C53B";
	
	public static String getSecret() {
		return SECRET;
	}
	
	public static String generateRandomPassword() {
		return "1234";
	}
	
	public static String hashPassword(String plainPassword) {
		return plainPassword;
	}
	
	public static void sendEmail(User user, String password) {
		// Logic to send emails
	}
}
