//package com.ey.repository;
//
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.ey.entities.Client;
//import com.ey.entities.Payment;
//import com.ey.entities.Vendor;
//
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//	Optional<Payment> findTopByEvent_IdOrderByCreatedAtDesc(Long eventId);
//
//	Optional<Payment> findByEvent_IdOrderByCreatedAtDesc(Long eventId);
//
//}

package com.ey.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Payment;
import com.ey.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByBooking_IdAndStatus(Long bookingId, PaymentStatus status);

    Optional<Payment> findTopByBooking_IdAndStatusOrderByCreatedAtDesc(
            Long bookingId, PaymentStatus status);

    List<Payment> findByBooking_Id(Long bookingId);

    // keep for client listing by event
    List<Payment> findByEvent_IdOrderByCreatedAtDesc(Long eventId);
}

