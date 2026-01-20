package com.ey.mapper;

import com.ey.dto.response.ReviewResponse;
import com.ey.entities.Client;
import com.ey.entities.Review;
import com.ey.entities.Vendor;

public final class ReviewMapper {

	private ReviewMapper() {
	}

	public static ReviewResponse toDto(Review review) {
		ReviewResponse dto = new ReviewResponse();
		dto.setId(review.getId());
		dto.setRating(review.getRating());
		dto.setComment(review.getComment());
		dto.setCreatedAt(review.getCreatedAt());

		Vendor v = review.getVendor();
		if (v != null) {
			dto.setVendorId(v.getId());
		}

		Client c = review.getClient();
		if (c != null) {
			dto.setClientId(c.getId());
			dto.setClientName(c.getName());
			dto.setClientEmail(c.getEmail());
		}
		return dto;
	}
}
