package com.ey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Client;
import com.ey.entities.Payment;
import com.ey.entities.Vendor;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findTopByEvent_IdOrderByCreatedAtDesc(Long eventId);

	Optional<Payment> findByEvent_IdOrderByCreatedAtDesc(Long eventId);

}
