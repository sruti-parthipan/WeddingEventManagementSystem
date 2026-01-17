

package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.PaymentCreateRequest;
import com.ey.service.PaymentService;

@RestController
@RequestMapping("/api/client/payments")
public class ClientPaymentController {

    @Autowired
    private PaymentService paymentService;

    // Create payment (PENDING) for client's event
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentCreateRequest req, Authentication auth) {
        String email = auth.getName();
        return paymentService.createPayment(req, email);
    }

    // Get a payment by id (client can only view their own event's payments)
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getById(@PathVariable Long paymentId, Authentication auth) {
        String email = auth.getName();
        return paymentService.getPaymentById(paymentId, email);
    }

    // List payments for an event (must belong to the client)
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<?> listByEvent(@PathVariable Long eventId, Authentication auth) {
        String email = auth.getName();
        return paymentService.listPaymentsForEvent(eventId, email);
    }
}
