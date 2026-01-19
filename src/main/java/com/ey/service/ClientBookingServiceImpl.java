//package com.ey.service;
//
//import org.springframework.http.ResponseEntity;
//
//import com.ey.dto.request.BookingCreateRequest;
//
//import jakarta.validation.Valid;
//
//public class ClientBookingServiceImpl implements ClientBookingService{
//
//	@Override
//	public ResponseEntity<?> createBooking(@Valid BookingCreateRequest request, String name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}

package com.ey.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.dto.request.BookingCreateRequest;
import com.ey.dto.response.BookingResponse;
import com.ey.entities.Booking;
import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.entities.Vendor;
import com.ey.enums.BookingStatus;
import com.ey.enums.EventStatus;
import com.ey.exception.BookingCreationException;
import com.ey.exception.ClientUnauthorizedException;
import com.ey.exception.DuplicateBookingException;
import com.ey.exception.EventNotFoundException;
import com.ey.exception.VendorNotFoundException;
import com.ey.mapper.BookingMapper;
import com.ey.repository.BookingRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;
import com.ey.repository.VendorRepository;

//@Service
//public class ClientBookingServiceImpl implements ClientBookingService {
//
//    @Autowired private BookingRepository bookingRepository;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private VendorRepository vendorRepository;
//    @Autowired private ClientRepository clientRepository;
//    Logger logger = LoggerFactory.getLogger(ClientBookingServiceImpl.class);
//    @Override
//    @Transactional
//    public ResponseEntity<?> createBooking(BookingCreateRequest request, String email) {
//
//        // 401 if no authenticated client
//        Optional<Client> clientOpt = clientRepository.findByEmail(email);
//        if (clientOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not authenticated");
//        }
//        Client client = clientOpt.get();
//
//        // 404 Event not found / not owned by client
//        Optional<Event> eventOpt = eventRepository.findByIdAndClient_Id(request.getEventId(), client.getId());
//        if (eventOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
//        }
//        Event event = eventOpt.get();
//
//        // 404 Vendor not found
//        Optional<Vendor> vendorOpt = vendorRepository.findById(request.getVendorId());
//        if (vendorOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
//        }
//        Vendor vendor = vendorOpt.get();
//
//        // 409 already booked this vendor for this event
//        if (bookingRepository.existsByEvent_IdAndVendor_Id(event.getId(), vendor.getId())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Vendor already booked for this event");
//        }
//
//        // Create booking with REQUESTED; Event stays PLANNED
//        event.setStatus(EventStatus.PLANNED); // idempotent with your lifecycle
//        Booking booking = BookingMapper.toEntity(request, event, vendor);
//        booking.setStatus(BookingStatus.REQUESTED);
//        Booking saved = bookingRepository.save(booking);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(BookingMapper.toResponse(saved));
//    }

@Service
public class ClientBookingServiceImpl implements ClientBookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private ClientRepository clientRepository;

 Logger logger = LoggerFactory.getLogger(ClientBookingServiceImpl.class);

    @Override
    @Transactional
    public ResponseEntity<?> createBooking(BookingCreateRequest request, String email) {

        logger.info("Create booking request received by client email=" + email
                + ", eventId=" + request.getEventId() + ", vendorId=" + request.getVendorId());

        try {
            // 401 if no authenticated client
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client not authenticated email=" + email);
                        return new ClientUnauthorizedException("Client not authenticated");
                    });

            // 404 Event not found / not owned by client
            Event event = eventRepository.findByIdAndClient_Id(request.getEventId(), client.getId())
                    .orElseThrow(() -> {
                        logger.warn("Event not found or not owned. eventId=" + request.getEventId()
                                + ", clientId=" + client.getId());
                        return new EventNotFoundException("Event not found");
                    });

            // 404 Vendor not found
            Vendor vendor = vendorRepository.findById(request.getVendorId())
                    .orElseThrow(() -> {
                        logger.warn("Vendor not found vendorId=" + request.getVendorId());
                        return new VendorNotFoundException("Vendor not found");
                    });

            // 409 already booked this vendor for this event
            if (bookingRepository.existsByEvent_IdAndVendor_Id(event.getId(), vendor.getId())) {
                logger.warn("Duplicate booking detected eventId=" + event.getId()
                        + ", vendorId=" + vendor.getId());
                throw new DuplicateBookingException("Vendor already booked for this event");
            }

            // Create booking with REQUESTED; Event stays PLANNED
            event.setStatus(EventStatus.PLANNED); // idempotent with your lifecycle

            Booking booking = BookingMapper.toEntity(request, event, vendor);
            booking.setStatus(BookingStatus.REQUESTED);

            Booking saved = bookingRepository.save(booking);

            logger.info("Booking created successfully id=" + saved.getId()
                    + ", eventId=" + saved.getEvent().getId()
                    + ", vendorId=" + saved.getVendor().getId()
                    + ", status=" + saved.getStatus());

            BookingResponse resp = BookingMapper.toResponse(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (ClientUnauthorizedException | EventNotFoundException
                 | VendorNotFoundException | DuplicateBookingException ex) {
            // Known cases -> bubble up to global handler later
            throw ex;

        

        } catch (Exception ex) {
            logger.error("Unexpected error while creating booking for email=" + email + ": " + ex.getMessage(), ex);
            throw new BookingCreationException("Failed to create booking");
        }
    }
}


