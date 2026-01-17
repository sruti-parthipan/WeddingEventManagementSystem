package com.ey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.request.EventRegistrationRequest;
import com.ey.dto.request.EventUpdateRequest;
import com.ey.dto.response.EventRegistrationResponse;
import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.enums.EventStatus;
import com.ey.mapper.EventMapper;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;
@Service
public class ClientEventServiceImpl implements ClientEventService{

@Autowired 
private EventRepository eventRepository;
    @Autowired 
    private ClientRepository clientRepository;

	 @Override
	    public ResponseEntity<?> createEvent(EventRegistrationRequest request, String email) {

	        Client client = clientRepository.findByEmail(email).orElse(null);
	        if (client == null)
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");

	        Event event = EventMapper.toEntity(request);
	        event.setClient(client);

	        Event saved = eventRepository.save(event);
	        return 
	        		ResponseEntity.status(HttpStatus.CREATED).body(EventMapper.toResponse(saved));
	    }

@Override
    public ResponseEntity<?> listMyEvents(String email) {
        Client client = clientRepository.findByEmail(email).orElse(null);
        List<Event> events = eventRepository.findByClientOrderByCreatedAtDesc(client);
        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for( Event event:events) {
        eventList.add(EventMapper.toResponse(event));
        }
        return ResponseEntity.ok(eventList);
    }



@Override
    public ResponseEntity<?> listMyEventsByTitle(String title, String email) {
        Client client = clientRepository.findByEmail(email).orElse(null);
        List<Event> events = eventRepository.findByClientAndTitleIgnoreCase(client, title);
        if (events.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this title");

        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for( Event event:events) {
        eventList.add(EventMapper.toResponse(event));
        }
        return ResponseEntity.ok(eventList);
}

@Override
   public ResponseEntity<?> listMyEventsByStatus(String status, String email) {
       Client client = clientRepository.findByEmail(email).orElse(null);

       EventStatus st;
       try { st = EventStatus.valueOf(status.toUpperCase()); }
       catch (Exception e) { return ResponseEntity.badRequest().body("Invalid status value"); }

       List<Event> events = eventRepository.findByClientAndStatus(client, st);
       if (events.isEmpty())
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this status");
       List<EventRegistrationResponse> eventList = new ArrayList<>();
       for( Event event:events) {
       eventList.add(EventMapper.toResponse(event));
       }
       return ResponseEntity.ok(eventList);

    }

@Override
    public ResponseEntity<?> listMyEventsByDate(String date, String email) {
        Client client = clientRepository.findByEmail(email).orElse(null);
        List<Event> events = eventRepository.findByClientAndEventDate(client, date);
        if (events.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found on this date");
        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for( Event event:events) {
        eventList.add(EventMapper.toResponse(event));
        }
        return ResponseEntity.ok(eventList);
    }

@Override
    public ResponseEntity<?> listMyEventsByVenue(String venue, String email) {
        Client client = clientRepository.findByEmail(email).orElse(null);
        List<Event> events = eventRepository.findByClientAndVenueIgnoreCase(client, venue);
        if (events.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this venue");
        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for( Event event:events) {
        eventList.add(EventMapper.toResponse(event));
        }
        return ResponseEntity.ok(eventList);
       
    }

@Override
    public ResponseEntity<?> updateEvent(Long id, EventUpdateRequest request, String email) {
        Client client = clientRepository.findByEmail(email).orElse(null);

        Optional<Event> opt = eventRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        Event event = opt.get();

        if (!event.getClient().getId().equals(client.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");

        EventMapper.applyUpdate(event, request);


Event saved = eventRepository.save(event);

        return ResponseEntity.ok(EventMapper.toResponse(saved));
    }




@Override
public ResponseEntity<?> deleteEvent(Long id, String email) {
    // 1) Resolve client
    Optional<Client> clientOpt = clientRepository.findByEmail(email);
    if (clientOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
    }
    Client client = clientOpt.get();

    // 2) Load event
    Optional<Event> eventOpt = eventRepository.findById(id);
    if (eventOpt.isEmpty()) {

return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
    }
    Event event = eventOpt.get();

    // 3) Ownership check
    if (!event.getClient().getId().equals(client.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this event");
    }

 // 4) Delete
     eventRepository.delete(event);

     // 5) Return 200 OK with a simple message
     return ResponseEntity.ok("Event deleted successfully");
 }




    }



