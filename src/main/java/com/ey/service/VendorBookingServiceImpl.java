package com.ey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.response.BookingResponse;
import com.ey.dto.response.BookingStatusUpdateResponse;
import com.ey.entities.Booking;
import com.ey.entities.Payment;
import com.ey.entities.Vendor;
import com.ey.enums.BookingStatus;
import com.ey.enums.EventStatus;
import com.ey.enums.PaymentStatus;
import com.ey.repository.BookingRepository;
import com.ey.repository.EventRepository;
import com.ey.repository.PaymentRepository;
import com.ey.repository.VendorRepository;

import jakarta.transaction.Transactional;
@Service
public class VendorBookingServiceImpl implements VendorBookingService {

@Autowired private BookingRepository bookingRepository;
    @Autowired private VendorRepository vendorRepository;
@Autowired private PaymentRepository paymentRepository;
@Autowired private EventRepository eventRepository;
	@Override
	 @Transactional
	public ResponseEntity<?> listMyBookings(String name) {
		// TODO Auto-generated method stub

Vendor me = vendorRepository.findByContactEmail(name).orElse(null);
        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor not authenticated");
        }
        List<Booking> list = bookingRepository
                .findAll() // replace with a dedicated query if you created one (e.g., findByVendor_IdOrderByCreatedAtDesc)
                .stream()
                .filter(b -> b.getVendor().getId().equals(me.getId()))
                .toList();

List<BookingResponse> out = list.stream().map(b -> {
            BookingResponse r = new BookingResponse();
            r.setId(b.getId());
            r.setEventId(b.getEvent().getId());
            r.setVendorId(b.getVendor().getId());
            r.setAgreedPrice(b.getAgreedPrice());
            r.setStatus(b.getStatus());
            r.setCreatedAt(b.getCreatedAt());
            r.setUpdatedAt(b.getUpdatedAt());
            return r;
        }).toList();
return ResponseEntity.ok(out);


		
	}

@Override
    @Transactional
    public ResponseEntity<?> acceptBooking(Long bookingId, String vendorEmail) {
        Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor not authenticated");
        }

        Booking b = bookingRepository.findById(bookingId).orElse(null);
        if (b == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");

}
        if (!b.getVendor().getId().equals(me.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this booking");
        }
        if (b.getStatus() != BookingStatus.REQUESTED) {
            return ResponseEntity.badRequest().body("Booking cannot be accepted in current state");
        }

// ACCEPT RULE:
        // If vendor accepts, set Booking=CONFIRMED and Event=CONFIRMED
        b.setStatus(BookingStatus.CONFIRMED);
        b.getEvent().setStatus(EventStatus.CONFIRMED);

        Booking saved = bookingRepository.save(b);


BookingStatusUpdateResponse resp = new BookingStatusUpdateResponse();
resp.setId(saved.getId());
resp.setStatus(saved.getStatus());
resp.setUpdatedAt(saved.getUpdatedAt());

return ResponseEntity.ok(resp);

    }

@Override
    @Transactional
    public ResponseEntity<?> cancelBooking(Long bookingId, String vendorEmail) {
        Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor not authenticated");
        }

Booking b = bookingRepository.findById(bookingId).orElse(null);
        if (b == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
        if (!b.getVendor().getId().equals(me.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this booking");
        }
        if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("Booking cannot be cancelled");

        }

            // CANCEL RULE:
            // Booking only -> CANCELLED. Event is not changed (client can rebook another vendor)
            b.setStatus(BookingStatus.CANCELLED);

            Booking saved = bookingRepository.save(b);


BookingStatusUpdateResponse response = new BookingStatusUpdateResponse();
response.setId(saved.getId());
response.setStatus(saved.getStatus());
response.setUpdatedAt(saved.getUpdatedAt());

return ResponseEntity.ok(response);
}



@Override
@Transactional
public ResponseEntity<?> confirmPayment(Long bookingId, String vendorEmail) {

    // 401 — vendor must be authenticated
    Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
    if (me == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor not authenticated");
    }

    // 404 — booking must exist
    Booking b = bookingRepository.findById(bookingId).orElse(null);
    if (b == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
    }

    // 403 — booking must belong to this vendor
    if (!b.getVendor().getId().equals(me.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to confirm payment for this booking");
    }

    // (Optional) 400 — booking should be CONFIRMED before payment confirmation
    if (b.getStatus() != BookingStatus.CONFIRMED) {
        return ResponseEntity.badRequest().body("Payment can be confirmed only for CONFIRMED bookings");
    }

    // 404 — payment must exist for the booking's event
    Long eventId = b.getEvent().getId();
    Payment p = paymentRepository.findTopByEvent_IdOrderByCreatedAtDesc(eventId).orElse(null);
    if (p == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No payment found for this event");
    }

    // 400 — payment must be PENDING
    if (p.getStatus() != PaymentStatus.PENDING) {
        return ResponseEntity.badRequest().body("Payment already processed");
    }

    // Update payment & event & booking
    p.setStatus(PaymentStatus.SUCCESS);
    paymentRepository.save(p);

    b.getEvent().setStatus(EventStatus.IN_PROGRESS); // or EventStatus.CONFIRMED if you prefer
    eventRepository.save(b.getEvent());
b.setStatus(BookingStatus.CONFIRMED);
bookingRepository.save(b);
    // Minimal OK response (you can return a PaymentResponse if you want)
    return ResponseEntity.ok("Payment confirmed successfully");
}


}




