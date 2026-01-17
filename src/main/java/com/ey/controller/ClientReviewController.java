

package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.ClientReviewRequest;
import com.ey.service.ClientReviewService;

@RestController
@RequestMapping("/api/client/reviews")
public class ClientReviewController {

    @Autowired
    private ClientReviewService reviewService;

    // Client creates a review; event status becomes COMPLETED
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ClientReviewRequest req, Authentication auth) {
        String email = auth.getName();
        return reviewService.createReview(req, email);
    }
}
