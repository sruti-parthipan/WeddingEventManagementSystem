//
//
//package com.ey.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import com.ey.dto.request.PaymentCreateRequest;
//
//import com.ey.dto.response.PaymentResponse;
//import com.ey.entities.Client;
//import com.ey.entities.Event;
//import com.ey.entities.Payment;
//import com.ey.enums.EventStatus;
//import com.ey.enums.PaymentStatus;
//import com.ey.repository.ClientRepository;
//import com.ey.repository.EventRepository;
//import com.ey.repository.PaymentRepository;
//
//import jakarta.transaction.Transactional;
//
//@Service
//public class PaymentServiceImpl implements PaymentService {
//
//    @Autowired private ClientRepository clientRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private PaymentRepository paymentRepository;
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> createPayment(PaymentCreateRequest req, String clientEmail) {
//
//        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
//        if (client == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//        }
//
//        if (req == null || req.getEventId() == null || req.getAmount() == null || req.getAmount() <= 0) {
//            return ResponseEntity.badRequest().body("Invalid request: eventId and positive amount are required");
//        }
//
//        Event event = eventRepository.findById(req.getEventId()).orElse(null);
//        if (event == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//        }
//
//        // Ensure this event belongs to the current client
//        if (event.getClient() == null || !event.getClient().getId().equals(client.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to create payment for this event");
//        }
//
//        // Optional rule: allow payment only when event is CONFIRMED
//        if (event.getStatus() != EventStatus.CONFIRMED) {
//            return ResponseEntity.badRequest().body("Payment allowed only for CONFIRMED events");
//        }
//
//        // Prevent duplicate pending payments for same event (due to unique (event_id,status))
//        Payment latest = paymentRepository.findTopByEvent_IdOrderByCreatedAtDesc(event.getId()).orElse(null);
//        if (latest != null && latest.getStatus() == PaymentStatus.PENDING) {
//            return ResponseEntity.badRequest().body("A pending payment already exists for this event");
//        }
//
//        Payment p = new Payment();
//        p.setEvent(event);
//        p.setAmount(req.getAmount());
//        p.setReference(req.getReference() != null ? req.getReference().trim() : "");
//        p.setStatus(PaymentStatus.PENDING);
//
//        Payment saved = paymentRepository.save(p);
//
//        return ResponseEntity.ok(toResponse(saved));
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> getPaymentById(Long paymentId, String clientEmail) {
//
//        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
//        if (client == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//        }
//
//        Payment p = paymentRepository.findById(paymentId).orElse(null);
//        if (p == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
//        }
//
//        // Ensure the payment's event belongs to this client
//        if (p.getEvent() == null || p.getEvent().getClient() == null ||
//            !p.getEvent().getClient().getId().equals(client.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to view this payment");
//        }
//
//        return ResponseEntity.ok(toResponse(p));
//    }
//
//    @Override
//    @Transactional
//    public ResponseEntity<?> listPaymentsForEvent(Long eventId, String clientEmail) {
//
//        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
//        if (client == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//        }
//
//        Event event = eventRepository.findById(eventId).orElse(null);
//        if (event == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//        }
//
//        if (event.getClient() == null || !event.getClient().getId().equals(client.getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to view payments for this event");
//        }
//
//        List<PaymentResponse> list = paymentRepository
//                .findByEvent_IdOrderByCreatedAtDesc(eventId)
//                .stream()
//                .map(this::toResponse)
//                .toList();
//
//        return ResponseEntity.ok(list);
//    }
//
//    private PaymentResponse toResponse(Payment p) {
//        PaymentResponse r = new PaymentResponse();
//        r.setId(p.getId());
//        r.setEventId(p.getEvent() != null ? p.getEvent().getId() : null);
//        r.setAmount(p.getAmount());
//        r.setStatus(p.getStatus());
//        r.setReference(p.getReference());
//        r.setCreatedAt(p.getCreatedAt());
//        
//        return r;
//    }
//}

package com.ey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.request.PaymentCreateRequest;
import com.ey.dto.response.PaymentResponse;
import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.entities.Payment;
import com.ey.entities.Booking;
import com.ey.enums.BookingStatus;
import com.ey.enums.EventStatus;
import com.ey.enums.PaymentStatus;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;
import com.ey.repository.PaymentRepository;
import com.ey.repository.BookingRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired private ClientRepository clientRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private BookingRepository bookingRepository; // ✅ add this

    @Override
    @Transactional
    public ResponseEntity<?> createPayment(PaymentCreateRequest req, String clientEmail) {

        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
        }

        if (req == null || req.getBookingId() == null || req.getAmount() == null || req.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("Invalid request: bookingId and positive amount are required");
        }

        Booking booking = bookingRepository.findById(req.getBookingId()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        // Ensure the booking's event belongs to the current client
        if (booking.getEvent() == null || booking.getEvent().getClient() == null ||
            !booking.getEvent().getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to create payment for this booking");
        }

        // Allow payment only when booking is CONFIRMED (not just event)
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return ResponseEntity.badRequest().body("Payment allowed only for CONFIRMED bookings");
        }

        // Prevent duplicate pending payments for the same booking
        if (paymentRepository.existsByBooking_IdAndStatus(booking.getId(), PaymentStatus.PENDING)) {
            return ResponseEntity.badRequest().body("A pending payment already exists for this booking");
        }

        Payment p = new Payment();
        p.setBooking(booking);                  // ✅ critical link
        p.setEvent(booking.getEvent());         // keep for event listing
        p.setAmount(req.getAmount());
        p.setReference(req.getReference() != null ? req.getReference().trim() : "PAY" + System.currentTimeMillis());
        p.setStatus(PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(p);

        return ResponseEntity.ok(toResponse(saved));
    }

    @Override
    @Transactional
    public ResponseEntity<?> getPaymentById(Long paymentId, String clientEmail) {

        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
        }

        Payment p = paymentRepository.findById(paymentId).orElse(null);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        }

        if (p.getEvent() == null || p.getEvent().getClient() == null ||
            !p.getEvent().getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to view this payment");
        }

        return ResponseEntity.ok(toResponse(p));
    }

    @Override
    @Transactional
    public ResponseEntity<?> listPaymentsForEvent(Long eventId, String clientEmail) {

        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        if (event.getClient() == null || !event.getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to view payments for this event");
        }

        List<PaymentResponse> list = paymentRepository
                .findByEvent_IdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setBookingId(p.getBooking() != null ? p.getBooking().getId() : null); // ✅ include bookingId
        r.setEventId(p.getEvent() != null ? p.getEvent().getId() : null);
        r.setAmount(p.getAmount());
        r.setStatus(p.getStatus());
        r.setReference(p.getReference());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}

