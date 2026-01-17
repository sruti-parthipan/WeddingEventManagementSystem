package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	boolean existsByEvent_IdAndVendor_Id(Long id, Long id2);

}
