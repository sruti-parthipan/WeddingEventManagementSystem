

package com.ey.entities;

import com.ey.enums.PaymentStatus;
import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments",
       indexes = {
           @Index(name = "idx_payment_event", columnList = "event_id"),
           @Index(name = "idx_payment_status", columnList = "status")
       },
       uniqueConstraints = {
           // If you only allow one successful payment per event:
           // remove this constraint if you want multiple payments/instalments
           @UniqueConstraint(name = "uk_payment_event_success", columnNames = {"event_id", "status"})
       })

public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING; // in this simple flow we mark SUCCESS

    @Column(nullable = false, length = 64)
    private String reference;

    @CreationTimestamp
    @Column(updatable = false)
    private String createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
    
}
