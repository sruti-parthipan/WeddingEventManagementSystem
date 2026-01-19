//package com.ey.service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import com.ey.dto.request.EventRegistrationRequest;
//import com.ey.dto.request.EventUpdateRequest;
//import com.ey.dto.response.EventRegistrationResponse;
//import com.ey.entities.Client;
//import com.ey.entities.Event;
//import com.ey.enums.EventStatus;
//import com.ey.mapper.EventMapper;
//import com.ey.repository.ClientRepository;
//import com.ey.repository.EventRepository;
//@Service
//public class ClientEventServiceImpl implements ClientEventService{
//
//@Autowired 
//private EventRepository eventRepository;
//    @Autowired 
//    private ClientRepository clientRepository;
//
//	 @Override
//	    public ResponseEntity<?> createEvent(EventRegistrationRequest request, String email) {
//
//	        Client client = clientRepository.findByEmail(email).orElse(null);
//	        if (client == null)
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//
//	        Event event = EventMapper.toEntity(request);
//	        event.setClient(client);
//
//	        Event saved = eventRepository.save(event);
//	        return 
//	        		ResponseEntity.status(HttpStatus.CREATED).body(EventMapper.toResponse(saved));
//	    }
//
//@Override
//    public ResponseEntity<?> listMyEvents(String email) {
//        Client client = clientRepository.findByEmail(email).orElse(null);
//        List<Event> events = eventRepository.findByClientOrderByCreatedAtDesc(client);
//        List<EventRegistrationResponse> eventList = new ArrayList<>();
//        for( Event event:events) {
//        eventList.add(EventMapper.toResponse(event));
//        }
//        return ResponseEntity.ok(eventList);
//    }
//
//
//
//@Override
//    public ResponseEntity<?> listMyEventsByTitle(String title, String email) {
//        Client client = clientRepository.findByEmail(email).orElse(null);
//        List<Event> events = eventRepository.findByClientAndTitleIgnoreCase(client, title);
//        if (events.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this title");
//
//        List<EventRegistrationResponse> eventList = new ArrayList<>();
//        for( Event event:events) {
//        eventList.add(EventMapper.toResponse(event));
//        }
//        return ResponseEntity.ok(eventList);
//}
//
//@Override
//   public ResponseEntity<?> listMyEventsByStatus(String status, String email) {
//       Client client = clientRepository.findByEmail(email).orElse(null);
//
//       EventStatus st;
//       try { st = EventStatus.valueOf(status.toUpperCase()); }
//       catch (Exception e) { return ResponseEntity.badRequest().body("Invalid status value"); }
//
//       List<Event> events = eventRepository.findByClientAndStatus(client, st);
//       if (events.isEmpty())
//           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this status");
//       List<EventRegistrationResponse> eventList = new ArrayList<>();
//       for( Event event:events) {
//       eventList.add(EventMapper.toResponse(event));
//       }
//       return ResponseEntity.ok(eventList);
//
//    }
//
//@Override
//    public ResponseEntity<?> listMyEventsByDate(String date, String email) {
//        Client client = clientRepository.findByEmail(email).orElse(null);
//        List<Event> events = eventRepository.findByClientAndEventDate(client, date);
//        if (events.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found on this date");
//        List<EventRegistrationResponse> eventList = new ArrayList<>();
//        for( Event event:events) {
//        eventList.add(EventMapper.toResponse(event));
//        }
//        return ResponseEntity.ok(eventList);
//    }
//
//@Override
//    public ResponseEntity<?> listMyEventsByVenue(String venue, String email) {
//        Client client = clientRepository.findByEmail(email).orElse(null);
//        List<Event> events = eventRepository.findByClientAndVenueIgnoreCase(client, venue);
//        if (events.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with this venue");
//        List<EventRegistrationResponse> eventList = new ArrayList<>();
//        for( Event event:events) {
//        eventList.add(EventMapper.toResponse(event));
//        }
//        return ResponseEntity.ok(eventList);
//       
//    }
//
//@Override
//    public ResponseEntity<?> updateEvent(Long id, EventUpdateRequest request, String email) {
//        Client client = clientRepository.findByEmail(email).orElse(null);
//
//        Optional<Event> opt = eventRepository.findById(id);
//        if (opt.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//
//        Event event = opt.get();
//
//        if (!event.getClient().getId().equals(client.getId()))
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
//
//        EventMapper.applyUpdate(event, request);
//
//
//Event saved = eventRepository.save(event);
//
//        return ResponseEntity.ok(EventMapper.toResponse(saved));
//    }
//
//
//
//
//@Override
//public ResponseEntity<?> deleteEvent(Long id, String email) {
//    // 1) Resolve client
//    Optional<Client> clientOpt = clientRepository.findByEmail(email);
//    if (clientOpt.isEmpty()) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//    }
//    Client client = clientOpt.get();
//
//    // 2) Load event
//    Optional<Event> eventOpt = eventRepository.findById(id);
//    if (eventOpt.isEmpty()) {
//
//return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//    }
//    Event event = eventOpt.get();
//
//    // 3) Ownership check
//    if (!event.getClient().getId().equals(client.getId())) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this event");
//    }
//
// // 4) Delete
//     eventRepository.delete(event);
//
//     // 5) Return 200 OK with a simple message
//     return ResponseEntity.ok("Event deleted successfully");
// }
//
//
//
//
//    }
//
//
package com.ey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.ey.exception.EventNotFoundException;
import com.ey.exception.InvalidStatusException;
import com.ey.exception.NoEventsFoundException;
import com.ey.exception.NotAuthorizedException;
import com.ey.exception.EventCreateException;
import com.ey.exception.EventUpdateException;
import com.ey.exception.EventDeleteException;
import com.ey.mapper.EventMapper;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ClientEventServiceImpl implements ClientEventService {

    @Autowired 
    private EventRepository eventRepository;
    @Autowired 
    private ClientRepository clientRepository;

    private static final Logger logger = LoggerFactory.getLogger(ClientEventServiceImpl.class);

    @Override
    @Transactional
    public ResponseEntity<?> createEvent(EventRegistrationRequest request, String email) {

        logger.info("Create event request received email=" + email + ", title=" + request.getTitle());

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client not authenticated email=" + email);
                        return new ClientUnauthorizedException("Client not authenticated");
                    });

            Event event = EventMapper.toEntity(request);
            event.setClient(client);

            Event saved = eventRepository.save(event);

            logger.info("Event created successfully id=" + saved.getId() + ", clientId=" + client.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(EventMapper.toResponse(saved));

        } catch (ClientUnauthorizedException ex) {
            throw ex;

        

        } catch (Exception ex) {
            logger.error("Unexpected error while creating event for email=" + email + ": " + ex.getMessage(), ex);
            throw new EventCreateException("Failed to create event");
        }
    }

    @Override
    public ResponseEntity<?> listMyEvents(String email) {
        logger.info("List my events request received email=" + email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client not authenticated email=" + email);
                    return new ClientUnauthorizedException("Client not authenticated");
                });

        List<Event> events = eventRepository.findByClientOrderByCreatedAtDesc(client);
        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for (Event event : events) {
            eventList.add(EventMapper.toResponse(event));
        }

        logger.info("Events fetched successfully count=" + eventList.size() + " for clientId=" + client.getId());
        return ResponseEntity.ok(eventList);
    }

    @Override
    public ResponseEntity<?> listMyEventsByTitle(String title, String email) {
        logger.info("List my events by title request title=" + title + ", email=" + email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client not authenticated email=" + email);
                    return new ClientUnauthorizedException("Client not authenticated");
                });

        List<Event> events = eventRepository.findByClientAndTitleIgnoreCase(client, title);
        if (events.isEmpty()) {
            logger.warn("No events found with this title title=" + title + ", clientId=" + client.getId());
            throw new NoEventsFoundException("No events found with this title");
        }

        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for (Event event : events) {
            eventList.add(EventMapper.toResponse(event));
        }

        logger.info("Events by title fetched successfully count=" + eventList.size());
        return ResponseEntity.ok(eventList);
    }

    @Override
    public ResponseEntity<?> listMyEventsByStatus(String status, String email) {
        logger.info("List my events by status request status=" + status + ", email=" + email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client not authenticated email=" + email);
                    return new ClientUnauthorizedException("Client not authenticated");
                });

        EventStatus st;
        try {
            st = EventStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            logger.warn("Invalid status value provided=" + status);
            throw new InvalidStatusException("Invalid status value");
        }

        List<Event> events = eventRepository.findByClientAndStatus(client, st);
        if (events.isEmpty()) {
            logger.warn("No events found with this status status=" + status + ", clientId=" + client.getId());
            throw new NoEventsFoundException("No events found with this status");
        }

        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for (Event event : events) {
            eventList.add(EventMapper.toResponse(event));
        }

        logger.info("Events by status fetched successfully count=" + eventList.size());
        return ResponseEntity.ok(eventList);
    }

    @Override
    public ResponseEntity<?> listMyEventsByDate(String date, String email) {
        logger.info("List my events by date request date=" + date + ", email=" + email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client not authenticated email=" + email);
                    return new ClientUnauthorizedException("Client not authenticated");
                });

        List<Event> events = eventRepository.findByClientAndEventDate(client, date);
        if (events.isEmpty()) {
            logger.warn("No events found on this date date=" + date + ", clientId=" + client.getId());
            throw new NoEventsFoundException("No events found on this date");
        }

        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for (Event event : events) {
            eventList.add(EventMapper.toResponse(event));
        }

        logger.info("Events by date fetched successfully count=" + eventList.size());
        return ResponseEntity.ok(eventList);
    }

    @Override
    public ResponseEntity<?> listMyEventsByVenue(String venue, String email) {
        logger.info("List my events by venue request venue=" + venue + ", email=" + email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client not authenticated email=" + email);
                    return new ClientUnauthorizedException("Client not authenticated");
                });

        List<Event> events = eventRepository.findByClientAndVenueIgnoreCase(client, venue);
        if (events.isEmpty()) {
            logger.warn("No events found with this venue venue=" + venue + ", clientId=" + client.getId());
            throw new NoEventsFoundException("No events found with this venue");
        }

        List<EventRegistrationResponse> eventList = new ArrayList<>();
        for (Event event : events) {
            eventList.add(EventMapper.toResponse(event));
        }

        logger.info("Events by venue fetched successfully count=" + eventList.size());
        return ResponseEntity.ok(eventList);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateEvent(Long id, EventUpdateRequest request, String email) {
        logger.info("Update event request id=" + id + ", email=" + email);

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client not authenticated email=" + email);
                        return new ClientUnauthorizedException("Client not authenticated");
                    });

            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Event not found id=" + id);
                        return new EventNotFoundException("Event not found");
                    });

            if (!event.getClient().getId().equals(client.getId())) {
                logger.warn("Not authorized to update event id=" + id + ", clientId=" + client.getId());
                throw new NotAuthorizedException("Not authorized");
            }

            EventMapper.applyUpdate(event, request);
            Event saved = eventRepository.save(event);

            logger.info("Event updated successfully id=" + id);
            return ResponseEntity.ok(EventMapper.toResponse(saved));

        } catch (ClientUnauthorizedException | EventNotFoundException | NotAuthorizedException ex) {
            throw ex;

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            logger.warn("Data integrity violation while updating event id=" + id + ": " + ex.getMessage());
            throw new EventUpdateException("Data integrity violation while updating event");

        } catch (Exception ex) {
            logger.error("Unexpected error while updating event id=" + id + ": " + ex.getMessage(), ex);
            throw new EventUpdateException("Failed to update event");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteEvent(Long id, String email) {
        logger.info("Delete event request id=" + id + ", email=" + email);

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client not authenticated email=" + email);
                        return new ClientUnauthorizedException("Client not authenticated");
                    });

            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Event not found id=" + id);
                        return new EventNotFoundException("Event not found");
                    });

            if (!event.getClient().getId().equals(client.getId())) {
                logger.warn("Not authorized to delete event id=" + id + ", clientId=" + client.getId());
                throw new NotAuthorizedException("Not authorized to delete this event");
            }

            eventRepository.delete(event);

            logger.info("Event deleted successfully id=" + id);
            return ResponseEntity.ok("Event deleted successfully");

        } catch (ClientUnauthorizedException | EventNotFoundException | NotAuthorizedException ex) {
            throw ex;

        } catch (Exception ex) {
            logger.error("Unexpected error while deleting event id=" + id + ": " + ex.getMessage(), ex);
            throw new EventDeleteException("Failed to delete event");
        }
    }
}

