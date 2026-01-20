
package com.ey.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class EventRegistrationRequest {

	@NotBlank(message = "Title is required")
	private String title;

	// YYYY-MM-DD
	@NotBlank(message = "Event date is required")
	private String eventDate;

	// example: "10:00 am"
	@NotBlank(message = "Start time is required")
	private String eventStartTime;

	@NotBlank(message = "End time is required")
	private String eventEndTime;

	@NotBlank(message = "Venue is required")
	private String venue;

	@NotNull(message = "Capacity is required")
	@Positive(message = "Capacity must be positive")
	private Integer capacity;

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
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

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

}
