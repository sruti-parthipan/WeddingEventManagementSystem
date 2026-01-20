package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.AdminLoginRequest;
import com.ey.dto.request.AdminRegistrationRequest;

import jakarta.validation.Valid;

public interface AdminService {

	ResponseEntity<?> createAdmin(@Valid AdminRegistrationRequest request);

	ResponseEntity<?> loginAdmin(@Valid AdminLoginRequest request);

	ResponseEntity<?> getAllClients();

	ResponseEntity<?> getAllVendors();

	ResponseEntity<?> getClientById(Long id);

	ResponseEntity<?> getClientByName(String name);

	ResponseEntity<?> getClientByEmail(String email);

	ResponseEntity<?> getClientByPhone(String phone);

	ResponseEntity<?> getClientByAddress(String address);

	ResponseEntity<?> getClientByNameAndEmail(String name, String email);

}
