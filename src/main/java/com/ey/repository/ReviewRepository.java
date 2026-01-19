package com.ey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Client;
import com.ey.entities.Review;

public interface ReviewRepository  extends JpaRepository<Review, Long> {

	




    List<Review> findByVendor_IdOrderByCreatedAtDesc(Long vendorId);

    Optional<Review> findByEvent_IdAndVendor_Id(Long eventId, Long vendorId);

	List<Review> findAllByOrderByCreatedAtDesc();
}



