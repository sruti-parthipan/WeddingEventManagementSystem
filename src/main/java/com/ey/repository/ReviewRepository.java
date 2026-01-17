package com.ey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Review;

public interface ReviewRepository  extends JpaRepository<Review, Long> {

	Optional<Review >findByEvent_IdAndVendor_Id(Long id, Long id2); 

}
