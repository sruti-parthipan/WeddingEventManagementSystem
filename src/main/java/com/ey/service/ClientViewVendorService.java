package com.ey.service;

import org.springframework.http.ResponseEntity;

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

	ResponseEntity<?> getVendorsByServiceTypeAndBasePrice(String serviceType, Double basePrice);

	ResponseEntity<?> listReviewsForVendor(Long vendorId, String email);

	ResponseEntity<?> getRatingsForVendor(Long vendorId, String email);

	ResponseEntity<?> listAllVendorReviews(String email);

}
