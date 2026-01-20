package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.AdminLoginRequest;
import com.ey.dto.request.AdminRegistrationRequest;
import com.ey.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody AdminRegistrationRequest request) {
		return adminService.createAdmin(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request) {
		return adminService.loginAdmin(request);
	}


	@GetMapping("/clients")
	public ResponseEntity<?> getAllClients() {
		return adminService.getAllClients();
	}


	@GetMapping("/clients/{id}")
	public ResponseEntity<?> getClientById(@PathVariable Long id) {
		return adminService.getClientById(id);
	}


	@GetMapping("/clients/by-name/{name}")
	public ResponseEntity<?> getClientByName(@PathVariable String name) {
		return adminService.getClientByName(name);
	}

	@GetMapping("/clients/by-email/{email}")
	public ResponseEntity<?> getClientByEmail(@PathVariable String email) {
		return adminService.getClientByEmail(email);
	}

	@GetMapping("/clients/by-phone/{phone}")
	public ResponseEntity<?> getClientByPhone(@PathVariable String phone) {
		return adminService.getClientByPhone(phone);
	}


	@GetMapping("/clients/by-address/{address}")
	public ResponseEntity<?> getClientByAddress(@PathVariable String address) {
		return adminService.getClientByAddress(address);
	}


	@GetMapping("/clients/by-name-email/{name}/{email}")
	public ResponseEntity<?> getClientByNameAndEmail(@PathVariable String name, @PathVariable String email) {
		return adminService.getClientByNameAndEmail(name, email);
	}

	@GetMapping("/vendors")
	public ResponseEntity<?> getAllVendors() {
		return adminService.getAllVendors();
	}

}
