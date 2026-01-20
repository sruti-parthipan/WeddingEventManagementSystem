
package com.ey.mapper;

import com.ey.dto.request.BookingCreateRequest;
import com.ey.dto.response.BookingResponse;
import com.ey.entities.Booking;
import com.ey.entities.Event;
import com.ey.entities.Vendor;

public class BookingMapper {

	public static Booking toEntity(BookingCreateRequest req, Event event, Vendor vendor) {
		Booking booking = new Booking();
		booking.setEvent(event); // relationship
		booking.setVendor(vendor); // relationship
		booking.setAgreedPrice(req.getAgreedPrice());
		return booking;
	}

	public static BookingResponse toResponse(Booking booking) {
		BookingResponse resp = new BookingResponse();
		resp.setId(booking.getId());
		resp.setEventId(booking.getEvent().getId()); // expose IDs to client
		resp.setVendorId(booking.getVendor().getId());
		resp.setAgreedPrice(booking.getAgreedPrice());
		resp.setStatus(booking.getStatus());
		resp.setCreatedAt(booking.getCreatedAt());
		resp.setUpdatedAt(booking.getUpdatedAt());
		return resp;
	}
}
