package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.BookingCreateRequest;

import jakarta.validation.Valid;

public interface ClientBookingService {

	ResponseEntity<?> createBooking(@Valid BookingCreateRequest request, String name);

}
