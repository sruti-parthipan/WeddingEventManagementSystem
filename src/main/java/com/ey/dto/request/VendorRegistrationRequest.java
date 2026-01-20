package com.ey.dto.request;

import com.ey.enums.ServiceType;
import jakarta.validation.constraints.*;

public class VendorRegistrationRequest {

	@NotBlank(message = "Name is required")
	@Size(max = 120, message = "Name must be at most 120 characters")
	private String name;

	@NotNull(message = "Service type is required")
	private ServiceType serviceType; // CATERING, DECORATION, PHOTOGRAPHY, MUSIC, VENUE, TRANSPORT

	@NotBlank(message = "Contact email is required")
	@Email(message = "Invalid email format")
	@Size(max = 120, message = "Email must be at most 120 characters")
	private String contactEmail;

	@NotBlank(message = "Contact phone is required")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10–15 digits")
	private String contactPhone;

	@NotNull(message = "Base price is required")
	@PositiveOrZero(message = "Base price must be zero or positive")
	private Double basePrice;

	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
