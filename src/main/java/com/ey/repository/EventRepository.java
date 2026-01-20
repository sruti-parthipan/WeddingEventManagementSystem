package com.ey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.enums.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByClientOrderByCreatedAtDesc(Client client);

	List<Event> findByClientAndTitleIgnoreCase(Client client, String title);

	List<Event> findByClientAndStatus(Client client, EventStatus st);

	List<Event> findByClientAndEventDate(Client client, String date);

	List<Event> findByClientAndVenueIgnoreCase(Client client, String venue);

	Optional<Event> findByIdAndClient_Id(Long eventId, Long id);

	List<Event> findAllByOrderByCreatedAtDesc();

	List<Event> findByTitleIgnoreCase(String title);

	List<Event> findByStatus(EventStatus st);

	List<Event> findByEventDate(String date);

	List<Event> findByVenueIgnoreCase(String venue);

}
