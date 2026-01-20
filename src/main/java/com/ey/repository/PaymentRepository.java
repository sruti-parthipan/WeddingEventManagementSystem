
package com.ey.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Payment;
import com.ey.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	boolean existsByBooking_IdAndStatus(Long bookingId, PaymentStatus status);

	Optional<Payment> findTopByBooking_IdAndStatusOrderByCreatedAtDesc(Long bookingId, PaymentStatus status);

	List<Payment> findByBooking_Id(Long bookingId);

	// client listing by event
	List<Payment> findByEvent_IdOrderByCreatedAtDesc(Long eventId);
}
