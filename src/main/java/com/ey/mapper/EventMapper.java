
package com.ey.mapper;

import com.ey.dto.request.EventRegistrationRequest;
import com.ey.dto.request.EventUpdateRequest;
import com.ey.dto.response.EventRegistrationResponse;
import com.ey.entities.Event;

public class EventMapper {

	public static Event toEntity(EventRegistrationRequest req) {
		Event e = new Event();
		e.setTitle(req.getTitle());
		e.setEventDate(req.getEventDate());
		e.setEventStartTime(req.getEventStartTime());
		e.setEventEndTime(req.getEventEndTime());
		e.setVenue(req.getVenue());
		e.setCapacity(req.getCapacity());
		return e;
	}

	public static void applyUpdate(Event e, EventUpdateRequest req) {
		if (req.getVenue() != null)
			e.setVenue(req.getVenue());
		if (req.getCapacity() != null)
			e.setCapacity(req.getCapacity());
		if (req.getEventStartTime() != null)
			e.setEventStartTime(req.getEventStartTime());
		if (req.getEventEndTime() != null)
			e.setEventEndTime(req.getEventEndTime());
	}

	public static EventRegistrationResponse toResponse(Event e) {
		EventRegistrationResponse res = new EventRegistrationResponse();
		res.setId(e.getId());
		res.setTitle(e.getTitle());
		res.setEventDate(e.getEventDate());
		res.setEventStartTime(e.getEventStartTime());
		res.setEventEndTime(e.getEventEndTime());
		res.setVenue(e.getVenue());
		res.setCapacity(e.getCapacity());
		res.setStatus(e.getStatus());
		res.setCreatedAt(e.getCreatedAt());
		res.setUpdatedAt(e.getUpdatedAt());
		return res;
	}
}
