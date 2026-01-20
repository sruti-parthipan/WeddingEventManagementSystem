
package com.ey.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ey.exception.BookingNotFoundException;
import com.ey.exception.InvalidBookingStateException;
import com.ey.exception.NotAuthorizedException;
import com.ey.exception.PaymentAlreadyProcessedException;
import com.ey.exception.PaymentConfirmationException;
import com.ey.exception.PaymentNotFoundException;
import com.ey.exception.VendorUnauthorizedException;
import com.ey.repository.BookingRepository;
import com.ey.repository.EventRepository;
import com.ey.repository.PaymentRepository;
import com.ey.repository.VendorRepository;

import jakarta.transaction.Transactional;

@Service
public class VendorBookingServiceImpl implements VendorBookingService {

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private EventRepository eventRepository;

	private static final Logger logger = LoggerFactory.getLogger(VendorBookingServiceImpl.class);

	@Override
	@Transactional
	public ResponseEntity<?> listMyBookings(String name) {
		logger.info("List my bookings request started");

		Vendor me = vendorRepository.findByContactEmail(name).orElse(null);
		if (me == null) {
			logger.warn("Vendor not authenticated");
			throw new VendorUnauthorizedException("Vendor not authenticated");
		}

		List<Booking> list = bookingRepository.findAll()
				.stream().filter(b -> b.getVendor().getId().equals(me.getId())).toList();

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

		logger.info("Bookings fetched successfully");
		return ResponseEntity.ok(out);
	}

	@Override
	@Transactional
	public ResponseEntity<?> acceptBooking(Long bookingId, String vendorEmail) {
		logger.info("Accept booking request started");

		Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
		if (me == null) {
			logger.warn("Vendor not authenticated");
			throw new VendorUnauthorizedException("Vendor not authenticated");
		}

		Booking b = bookingRepository.findById(bookingId).orElse(null);
		if (b == null) {
			logger.warn("Booking not found");
			throw new BookingNotFoundException("Booking not found");
		}
		if (!b.getVendor().getId().equals(me.getId())) {
			logger.warn("Not authorized to update this booking");
			throw new NotAuthorizedException("Not authorized to update this booking");
		}
		if (b.getStatus() != BookingStatus.REQUESTED) {
			logger.warn("Booking cannot be accepted in current state");
			throw new InvalidBookingStateException("Booking cannot be accepted in current state");
		}

		// ACCEPT RULE:
		b.setStatus(BookingStatus.CONFIRMED);
		b.getEvent().setStatus(EventStatus.CONFIRMED);

		Booking saved = bookingRepository.save(b);

		BookingStatusUpdateResponse resp = new BookingStatusUpdateResponse();
		resp.setId(saved.getId());
		resp.setStatus(saved.getStatus());
		resp.setUpdatedAt(saved.getUpdatedAt());

		logger.info("Booking accepted successfully");
		return ResponseEntity.ok(resp);
	}

	@Override
	@Transactional
	public ResponseEntity<?> cancelBooking(Long bookingId, String vendorEmail) {
		logger.info("Cancel booking request started");

		Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
		if (me == null) {
			logger.warn("Vendor not authenticated");
			throw new VendorUnauthorizedException("Vendor not authenticated");
		}

		Booking b = bookingRepository.findById(bookingId).orElse(null);
		if (b == null) {
			logger.warn("Booking not found");
			throw new BookingNotFoundException("Booking not found");
		}
		if (!b.getVendor().getId().equals(me.getId())) {
			logger.warn("Not authorized to update this booking");
			throw new NotAuthorizedException("Not authorized to update this booking");
		}
		if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) {
			logger.warn("Booking cannot be cancelled");
			throw new InvalidBookingStateException("Booking cannot be cancelled");
		}

		// CANCEL RULE:
		b.setStatus(BookingStatus.CANCELLED);

		Booking saved = bookingRepository.save(b);

		BookingStatusUpdateResponse response = new BookingStatusUpdateResponse();
		response.setId(saved.getId());
		response.setStatus(saved.getStatus());
		response.setUpdatedAt(saved.getUpdatedAt());

		logger.info("Booking cancelled successfully");
		return ResponseEntity.ok(response);
	}

	@Override
	@Transactional
	public ResponseEntity<?> confirmPayment(Long bookingId, String vendorEmail) {
		logger.info("Confirm payment request started");

		// 401 — vendor must be authenticated
		Vendor me = vendorRepository.findByContactEmail(vendorEmail).orElse(null);
		if (me == null) {
			logger.warn("Vendor not authenticated");
			throw new VendorUnauthorizedException("Vendor not authenticated");
		}

		// 404 — booking must exist
		Booking b = bookingRepository.findById(bookingId).orElse(null);
		if (b == null) {
			logger.warn("Booking not found");
			throw new BookingNotFoundException("Booking not found");
		}

		// 403 — booking must belong to this vendor
		if (!b.getVendor().getId().equals(me.getId())) {
			logger.warn("Not authorized to confirm payment for this booking");
			throw new NotAuthorizedException("Not authorized to confirm payment for this booking");
		}

		// 400 — booking should be CONFIRMED before payment confirmation
		if (b.getStatus() != BookingStatus.CONFIRMED) {
			logger.warn("Payment can be confirmed only for CONFIRMED bookings");
			throw new InvalidBookingStateException("Payment can be confirmed only for CONFIRMED bookings");
		}

		// 404 — payment must exist for the booking's event
		Long eventId = b.getEvent().getId();

		// NEW (RIGHT) — latest PENDING by booking
		Payment p = paymentRepository.findTopByBooking_IdAndStatusOrderByCreatedAtDesc(bookingId, PaymentStatus.PENDING)
				.orElse(null);
		if (p == null) {
			logger.warn("No pending payment found for this booking");
			throw new PaymentNotFoundException("No pending payment found for this booking");
		}



		// 400 — payment must be PENDING
		if (p.getStatus() != PaymentStatus.PENDING) {
			logger.warn("Payment already processed");
			throw new PaymentAlreadyProcessedException("Payment already processed");
		}

		try {
			// Update payment & event & booking
			p.setStatus(PaymentStatus.SUCCESS);
			paymentRepository.save(p);

			b.getEvent().setStatus(EventStatus.IN_PROGRESS); // keep your state as-is
			eventRepository.save(b.getEvent());

			b.setStatus(BookingStatus.CONFIRMED);
			bookingRepository.save(b);

			logger.info("Payment confirmed successfully");
			return ResponseEntity.ok("Payment confirmed successfully");
		} catch (Exception ex) {
			logger.error("Payment confirmation failed", ex);
			throw new PaymentConfirmationException("Payment confirmation failed");
		}
	}
}
