package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.EventRegistrationRequest;
import com.ey.dto.request.EventUpdateRequest;

public interface ClientEventService {

	ResponseEntity<?> createEvent(EventRegistrationRequest request, String email);

	ResponseEntity<?> listMyEvents(String email);

	ResponseEntity<?> listMyEventsByTitle(String title, String name);

	ResponseEntity<?> listMyEventsByStatus(String status, String email);

	ResponseEntity<?> listMyEventsByDate(String date, String email);

	ResponseEntity<?> listMyEventsByVenue(String venue, String name);

	ResponseEntity<?> updateEvent(Long id, EventUpdateRequest request, String email);

	ResponseEntity<?> deleteEvent(Long id, String name);

	ResponseEntity<?> getEventById(Long id, String email);

}
