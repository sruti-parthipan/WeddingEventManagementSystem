

package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.service.VendorBookingService;

@RestController
@RequestMapping("/api/vendor/bookings")
public class VendorBookingController {

    @Autowired
    private VendorBookingService vendorBookingService;

    // View all my bookings (optional helper you already use)
    @GetMapping
    public ResponseEntity<?> listMyBookings(Authentication auth) {
        return vendorBookingService.listMyBookings(auth.getName());
    }

    // ACCEPT booking -> Booking=CONFIRMED, Event=CONFIRMED (if rule is met)
    @PutMapping({"/{id}/accept", "/accept/{id}"})
    public ResponseEntity<?> accept(@PathVariable Long id, Authentication auth) {
        return vendorBookingService.acceptBooking(id, auth.getName());
    }

    // CANCEL booking -> Booking=CANCELLED (Event unchanged)
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication auth) {
        return vendorBookingService.cancelBooking(id, auth.getName());
    }

@PostMapping("/{id}/confirm-payment")
public ResponseEntity<?> confirmPayment(@PathVariable Long id, Authentication auth) {
    return vendorBookingService.confirmPayment(id, auth.getName());
}

}
