
package com.ey.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ey.enums.EventStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    // Client-provided fields (store as String)
    @Column(length = 20)
    private String eventDate;        // "2026-02-15"

    @Column(length = 20)
    private String eventStartTime;   // "10:00 am"

    @Column(length = 20)
    private String eventEndTime;     // "12:00 pm"

    @Column(length = 200)
    private String venue;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PLANNED;

    // SAME as Client/Vendor/Admin (Hibernate converts auto)
    @CreationTimestamp
    @Column(updatable = false)
    private String createdAt;

    @UpdateTimestamp
    @Column
    private String updatedAt;

    // Link to client
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getEventStartTime() { return eventStartTime; }
    public void setEventStartTime(String eventStartTime) { this.eventStartTime = eventStartTime; }

    public String getEventEndTime() { return eventEndTime; }
    public void setEventEndTime(String eventEndTime) { this.eventEndTime = eventEndTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
