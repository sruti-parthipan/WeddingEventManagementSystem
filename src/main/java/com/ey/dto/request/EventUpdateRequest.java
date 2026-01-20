
package com.ey.dto.request;

import jakarta.validation.constraints.Positive;

public class EventUpdateRequest {

	private String venue;

	@Positive(message = "Capacity must be positive")
	private Integer capacity;

	// e.g., "11:00 am"
	private String eventStartTime;

	// e.g., "11:00 pm"
	private String eventEndTime;

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(String eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public String getEventEndTime() {
		return eventEndTime;
	}

	public void setEventEndTime(String eventEndTime) {
		this.eventEndTime = eventEndTime;
	}
}
