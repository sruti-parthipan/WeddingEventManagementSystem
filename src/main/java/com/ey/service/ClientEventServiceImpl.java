
package com.ey.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.dto.request.EventRegistrationRequest;
import com.ey.dto.request.EventUpdateRequest;
import com.ey.dto.response.EventRegistrationResponse;
import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.enums.EventStatus;
import com.ey.exception.ClientUnauthorizedException;
import com.ey.exception.EventCreateException;
import com.ey.exception.EventNotFoundException;
import com.ey.exception.InvalidStatusException;
import com.ey.exception.NoEventsFoundException;
import com.ey.exception.NotAuthorizedException;
import com.ey.mapper.EventMapper;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;

@Service
public class ClientEventServiceImpl implements ClientEventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private ClientRepository clientRepository;

	private static final Logger logger = LoggerFactory.getLogger(ClientEventServiceImpl.class);

	// ⭐ CHECK IF THE LOGGED-IN USER IS ADMIN
	private boolean isAdmin() {
		return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}

	// CREATE EVENT (CLIENT ONLY)

	@Override
	@Transactional
	public ResponseEntity<?> createEvent(EventRegistrationRequest request, String email) {

		logger.info("Create event request received email=" + email);

		try {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));

			Event event = EventMapper.toEntity(request);
			event.setClient(client);

			Event saved = eventRepository.save(event);

			return ResponseEntity.status(HttpStatus.CREATED).body(EventMapper.toResponse(saved));

		} catch (Exception ex) {
			throw new EventCreateException("Failed to create event");
		}
	}

	// LIST ALL MY EVENTS (ADMIN → ALL EVENTS)

	@Override
	public ResponseEntity<?> listMyEvents(String email) {

		boolean admin = isAdmin();
		List<Event> events;

		if (admin) {
			events = eventRepository.findAllByOrderByCreatedAtDesc(); // ⭐ ADMIN sees all
		} else {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));
			events = eventRepository.findByClientOrderByCreatedAtDesc(client);
		}

		List<EventRegistrationResponse> res = new ArrayList<>();
		for (Event e : events)
			res.add(EventMapper.toResponse(e));

		return ResponseEntity.ok(res);
	}

	// LIST BY TITLE (ADMIN - ALL)

	@Override
	public ResponseEntity<?> listMyEventsByTitle(String title, String email) {

		boolean admin = isAdmin();
		List<Event> events;

		if (admin) {
			events = eventRepository.findByTitleIgnoreCase(title);
		} else {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));
			events = eventRepository.findByClientAndTitleIgnoreCase(client, title);
		}

		if (events.isEmpty())
			throw new NoEventsFoundException("No events found with this title");

		List<EventRegistrationResponse> res = new ArrayList<>();
		for (Event e : events)
			res.add(EventMapper.toResponse(e));

		return ResponseEntity.ok(res);
	}

	// LIST BY STATUS (ADMIN - ALL)

	@Override
	public ResponseEntity<?> listMyEventsByStatus(String status, String email) {

		boolean admin = isAdmin();
		EventStatus st;

		try {
			st = EventStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			throw new InvalidStatusException("Invalid status value");
		}

		List<Event> events;

		if (admin) {
			events = eventRepository.findByStatus(st);
		} else {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));
			events = eventRepository.findByClientAndStatus(client, st);
		}

		if (events.isEmpty())
			throw new NoEventsFoundException("No events found with this status");

		List<EventRegistrationResponse> res = new ArrayList<>();
		for (Event e : events)
			res.add(EventMapper.toResponse(e));

		return ResponseEntity.ok(res);
	}

	// LIST BY DATE (ADMIN -ALL)

	@Override
	public ResponseEntity<?> listMyEventsByDate(String date, String email) {

		boolean admin = isAdmin();
		List<Event> events;

		if (admin) {
			events = eventRepository.findByEventDate(date);
		} else {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));

			events = eventRepository.findByClientAndEventDate(client, date);
		}

		if (events.isEmpty())
			throw new NoEventsFoundException("No events found on this date");

		List<EventRegistrationResponse> res = new ArrayList<>();
		for (Event e : events)
			res.add(EventMapper.toResponse(e));

		return ResponseEntity.ok(res);
	}

	// LIST BY VENUE (ADMIN -ALL)

	@Override
	public ResponseEntity<?> listMyEventsByVenue(String venue, String email) {

		boolean admin = isAdmin();
		List<Event> events;

		if (admin) {
			events = eventRepository.findByVenueIgnoreCase(venue);
		} else {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));
			events = eventRepository.findByClientAndVenueIgnoreCase(client, venue);
		}

		if (events.isEmpty())
			throw new NoEventsFoundException("No events found with this venue");

		List<EventRegistrationResponse> res = new ArrayList<>();
		for (Event e : events)
			res.add(EventMapper.toResponse(e));

		return ResponseEntity.ok(res);
	}

	// GET EVENT BY ID (ADMIN SEE ANY EVENT)

	@Override
	public ResponseEntity<?> getEventById(Long id, String email) {

		boolean admin = isAdmin();

		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));

		if (!admin) {
			Client client = clientRepository.findByEmail(email)
					.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));

			if (!event.getClient().getId().equals(client.getId()))
				throw new NotAuthorizedException("Not allowed");
		}

		return ResponseEntity.ok(EventMapper.toResponse(event));
	}

	// ------------------------------------------------------------------
	// UPDATE EVENT (CLIENT ONLY)
	// ------------------------------------------------------------------
	@Override
	@Transactional
	public ResponseEntity<?> updateEvent(Long id, EventUpdateRequest request, String email) {

		Client client = clientRepository.findByEmail(email)
				.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));

		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));

		if (!event.getClient().getId().equals(client.getId()))
			throw new NotAuthorizedException("Not authorized");

		EventMapper.applyUpdate(event, request);

		Event saved = eventRepository.save(event);

		return ResponseEntity.ok(EventMapper.toResponse(saved));
	}

	// DELETE EVENT (CLIENT ONLY)
	@Override
	@Transactional
	public ResponseEntity<?> deleteEvent(Long id, String email) {

		Client client = clientRepository.findByEmail(email)
				.orElseThrow(() -> new ClientUnauthorizedException("Client not authenticated"));

		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));

		if (!event.getClient().getId().equals(client.getId()))
			throw new NotAuthorizedException("Not authorized to delete this event");

		eventRepository.delete(event);

		return ResponseEntity.ok("Event deleted successfully");
	}
}
