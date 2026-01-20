
package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.EventRegistrationRequest;
import com.ey.dto.request.EventUpdateRequest;
import com.ey.service.ClientEventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/events")
public class ClientEventController {

	@Autowired
	private ClientEventService clientEventService;

	private String email(Authentication authentication) {
		return authentication.getName(); // JWT subject = email
	}

	@PostMapping
	public ResponseEntity<?> createEvent(@Valid @RequestBody EventRegistrationRequest request, Authentication auth) {
		return clientEventService.createEvent(request, auth.getName());
	}

	@GetMapping
	public ResponseEntity<?> listMyEvents(Authentication auth) {
		return clientEventService.listMyEvents(auth.getName());
	}

	@GetMapping("/by-id/{id}")
	public ResponseEntity<?> listMyEventsById(@PathVariable Long id, Authentication auth) {
		return clientEventService.getEventById(id, auth.getName());
	}

	@GetMapping("/title/{title}")
	public ResponseEntity<?> listMyEventsByTitle(@PathVariable String title, Authentication auth) {
		return clientEventService.listMyEventsByTitle(title, auth.getName());
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<?> listMyEventsByStatus(@PathVariable String status, Authentication auth) {
		return clientEventService.listMyEventsByStatus(status, auth.getName());
	}

	@GetMapping("/date/{eventDate}")
	public ResponseEntity<?> listMyEventsByDate(@PathVariable String eventDate, Authentication auth) {
		return clientEventService.listMyEventsByDate(eventDate, auth.getName());
	}

	@GetMapping("/venue/{venue}")
	public ResponseEntity<?> listMyEventsByVenue(@PathVariable String venue, Authentication auth) {
		return clientEventService.listMyEventsByVenue(venue, auth.getName());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest request,
			Authentication auth) {
		return clientEventService.updateEvent(id, request, auth.getName());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable Long id, Authentication auth) {
		return clientEventService.deleteEvent(id, auth.getName());
	}

}
