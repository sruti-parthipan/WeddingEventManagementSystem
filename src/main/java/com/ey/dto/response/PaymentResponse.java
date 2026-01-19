
package com.ey.dto.response;

import com.ey.enums.PaymentStatus;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long bookingId;        // ✅ NEW
    private Long eventId;
    private Double amount;
    private PaymentStatus status;
    private String reference;
    private LocalDateTime createdAt; // ✅ use LocalDateTime
    private LocalDateTime updatedAt; // ✅ add updatedAt

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

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
