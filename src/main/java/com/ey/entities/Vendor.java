package com.ey.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ey.enums.Role;
import com.ey.enums.ServiceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vendors", uniqueConstraints = { @UniqueConstraint(columnNames = "contactEmail") })
public class Vendor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	// CATERING, DECORATION, PHOTOGRAPHY, MUSIC, VENUE, TRANSPORT
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ServiceType serviceType;

	@Column(nullable = false, unique = true)
	private String contactEmail;

	private String contactPhone;

	private Double basePrice;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role = Role.VENDOR;

	@CreationTimestamp
	@Column(updatable = false)
	private String createdAt;

	@UpdateTimestamp
	private String updatedAt;

	public Vendor() {
	}

	public Vendor(Long id, String name, ServiceType serviceType, String contactEmail, String contactPhone,
			Double basePrice, String password, Role role, String createdAt, String updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.serviceType = serviceType;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.basePrice = basePrice;
		this.password = password;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

}
