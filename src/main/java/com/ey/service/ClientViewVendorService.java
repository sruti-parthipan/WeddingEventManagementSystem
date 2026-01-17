package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.enums.ServiceType;

public interface ClientViewVendorService {

	ResponseEntity<?> getAllVendors();

	ResponseEntity<?> getVendorById(Long id);

	ResponseEntity<?> getVendorByName(String name);

	ResponseEntity<?> getVendorByEmail(String email);

	ResponseEntity<?> getVendorByContactPhone(String phone);

	ResponseEntity<?> getVendorsByBasePriceGreaterThan(Double amount);

	ResponseEntity<?> getVendorsByBasePriceLessThan(Double amount);

	ResponseEntity<?> getVendorsByBasePriceBetween(Double min, Double max);

	ResponseEntity<?> getVendorsByServiceType(String serviceType);

}
