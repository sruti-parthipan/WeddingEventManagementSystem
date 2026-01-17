

package com.ey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.request.ClientReviewRequest;
import com.ey.dto.response.ClientReviewResponse;
import com.ey.dto.response.ClientReviewResponse;

import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.entities.Review;
import com.ey.entities.Vendor;
import com.ey.enums.BookingStatus;
import com.ey.enums.EventStatus;
import com.ey.repository.BookingRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;
import com.ey.repository.ReviewRepository;
import com.ey.repository.VendorRepository;

import jakarta.transaction.Transactional;

@Service
public class ClientReviewServiceImpl implements ClientReviewService {

    @Autowired private ClientRepository clientRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private BookingRepository bookingRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createReview(ClientReviewRequest req, String clientEmail) {

        // 401 — client must be authenticated
        Client client = clientRepository.findByEmail(clientEmail).orElse(null);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
        }

        // Basic validation
        if (req == null || req.getEventId() == null || req.getVendorId() == null ||
            req.getRating() == null || req.getRating() < 1 || req.getRating() > 5 ||
            req.getComment() == null || req.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request: eventId, vendorId, rating(1..5), comment required");
        }

        // 404 — event & vendor must exist
        Event event = eventRepository.findById(req.getEventId()).orElse(null);
        if (event == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        Vendor vendor = vendorRepository.findById(req.getVendorId()).orElse(null);
        if (vendor == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");

        // 403 — event must belong to this client
        if (event.getClient() == null || !event.getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to review this event");
        }

        // (Rule) One review per (event, vendor)
        if (reviewRepository.findByEvent_IdAndVendor_Id(event.getId(), vendor.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("You have already reviewed this vendor for this event");
        }

        // Optional rule — ensure there was a booking with this vendor for this event and it was completed
        // If you have booking <-> event relation, verify that combination:
        boolean bookingCompleted = bookingRepository.findAll().stream().anyMatch(b ->
                b.getEvent() != null && b.getEvent().getId().equals(event.getId()) &&
                b.getVendor() != null && b.getVendor().getId().equals(vendor.getId()) &&
                b.getStatus() == BookingStatus.CONFIRMED
        );
        if (!bookingCompleted) {
            return ResponseEntity.badRequest().body("Cannot review: booking with this vendor not completed for this event");
        }

        // Create review
        Review r = new Review();
        r.setClient(client);
        r.setVendor(vendor);
        r.setEvent(event);
        r.setRating(req.getRating());
        r.setComment(req.getComment().trim());

        Review saved = reviewRepository.save(r);

        // As per your requirement: When client gives review, set EVENT = COMPLETED
        event.setStatus(EventStatus.COMPLETED);
        eventRepository.save(event);

        // Build response
        ClientReviewResponse out = toResponse(saved);
        return ResponseEntity.ok(out);
    }

    private ClientReviewResponse toResponse(Review r) {
    	ClientReviewResponse o = new ClientReviewResponse();
        o.setId(r.getId());
        o.setEventId(r.getEvent().getId() );
        o.setVendorId(r.getVendor() .getId());
        o.setClientId(r.getClient() .getId() );
        o.setRating(r.getRating());
        o.setComment(r.getComment());
        o.setCreatedAt(r.getCreatedAt());
        o.setUpdatedAt(r.getUpdatedAt());
        return o;
    }
}
