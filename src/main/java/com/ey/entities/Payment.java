//
//
//package com.ey.entities;
//
//import com.ey.enums.PaymentStatus;
//import jakarta.persistence.*;
//
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "payments",
//       indexes = {
//           @Index(name = "idx_payment_event", columnList = "event_id"),
//           @Index(name = "idx_payment_status", columnList = "status")
//       },
//       uniqueConstraints = {
//           // If you only allow one successful payment per event:
//           // remove this constraint if you want multiple payments/instalments
//           @UniqueConstraint(name = "uk_payment_event_success", columnNames = {"event_id", "status"})
//       })
//
//public class Payment {
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "event_id", nullable = false)
//    private Event event;
//
//    @Column(nullable = false)
//    private Double amount;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 20)
//    private PaymentStatus status = PaymentStatus.PENDING; // in this simple flow we mark SUCCESS
//
//    @Column(nullable = false, length = 64)
//    private String reference;
//
//    @CreationTimestamp
//    @Column(updatable = false)
//    private String createdAt;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public Event getEvent() {
//		return event;
//	}
//
//	public void setEvent(Event event) {
//		this.event = event;
//	}
//
//	public Double getAmount() {
//		return amount;
//	}
//
//	public void setAmount(Double amount) {
//		this.amount = amount;
//	}
//
//	public PaymentStatus getStatus() {
//		return status;
//	}
//
//	public void setStatus(PaymentStatus status) {
//		this.status = status;
//	}
//
//	public String getReference() {
//		return reference;
//	}
//
//	public void setReference(String reference) {
//		this.reference = reference;
//	}
//
//	public String getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(String createdAt) {
//		this.createdAt = createdAt;
//	}
//    
//}

package com.ey.entities;

import com.ey.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payment_event", columnList = "event_id"),
        @Index(name = "idx_payment_booking", columnList = "booking_id"),
        @Index(name = "idx_payment_status", columnList = "status")
    },
    uniqueConstraints = {
        // ✅ one row per (booking,status) -> prevents two PENDING for the same booking
        @UniqueConstraint(name = "uk_payment_booking_status", columnNames = {"booking_id", "status"})
    }
)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ NEW: Payment belongs to a specific booking
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Keep event for convenience (lists by event)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, length = 64)
    private String reference;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
