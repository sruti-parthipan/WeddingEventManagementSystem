package com.ey.dto.request;

import jakarta.persistence.Column;

public class AdminLoginRequest {
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
