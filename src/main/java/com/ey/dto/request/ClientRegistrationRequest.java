package com.ey.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClientRegistrationRequest {

	@NotBlank(message = "Name is required")
	@Size(max = 100, message = "Name must be at most 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Size(max = 120, message = "Email must be at most 120 characters")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
	private String password;

	@NotBlank(message = "Phone is required")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10–15 digits")
	private String phone;

	@Size(max = 255, message = "Address must be at most 255 characters")
	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
