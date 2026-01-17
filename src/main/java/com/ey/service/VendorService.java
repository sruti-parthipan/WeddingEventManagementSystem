package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.VendorRegistrationRequest;

import jakarta.validation.Valid;

public interface VendorService {

	ResponseEntity<?> createVendor(@Valid VendorRegistrationRequest request);

}
