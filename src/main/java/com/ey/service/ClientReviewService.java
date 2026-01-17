package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.ClientReviewRequest;

public interface ClientReviewService {

	ResponseEntity<?> createReview(ClientReviewRequest req, String clientEmail);

}
