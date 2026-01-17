

package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ey.dto.request.BookingCreateRequest;
import com.ey.service.ClientBookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/bookings")
public class ClientBookingController {

    @Autowired
    private ClientBookingService clientBookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingCreateRequest request,
                                           Authentication auth) {
        // same auth pattern as your events controller
        return clientBookingService.createBooking(request, auth.getName());
    }
}
