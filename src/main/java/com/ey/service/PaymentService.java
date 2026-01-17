package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.PaymentCreateRequest;

public interface PaymentService {

	ResponseEntity<?> createPayment(PaymentCreateRequest req, String email);

	ResponseEntity<?> getPaymentById(Long paymentId, String clientEmail);

	ResponseEntity<?> listPaymentsForEvent(Long eventId, String clientEmail);

}
