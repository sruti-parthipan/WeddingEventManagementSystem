package com.ey.service;

import org.springframework.http.ResponseEntity;

public interface VendorBookingService {

	ResponseEntity<?> listMyBookings(String name);

	ResponseEntity<?> acceptBooking(Long bookingId, String vendorEmail);

	ResponseEntity<?> cancelBooking(Long bookingId, String vendorEmail);

	ResponseEntity<?> confirmPayment(Long id, String name);

}
