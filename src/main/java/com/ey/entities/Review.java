package com.ey.entities;




import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ey.enums.EventStatus;

import jakarta.persistence.*;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        // One review per (event, vendor) — adjust if you want multiple
        @UniqueConstraint(name = "uk_review_event_vendor", columnNames = {"event_id", "vendor_id"})
    },
    indexes = {
        @Index(name = "idx_review_vendor", columnList = "vendor_id"),
        @Index(name = "idx_review_event", columnList = "event_id")
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Client who writes the review (owner of the event)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Vendor being reviewed
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    // Event for which the review is written
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Integer rating; // e.g., 1..5

    @Column(nullable = false, length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private String createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    public Review() {}

    public Long getId() { return id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
