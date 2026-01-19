//package com.ey.dto.request;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//
//public class PaymentCreateRequest {
//
//@NotNull
//    private Long eventId;
//
//    @NotNull
//    @Min(1)
//    private Double amount;
//
//    @NotNull
//    @Size(min = 3, max = 64)
//    private String reference; // e.g., UPI/Paytm/TxRef
//
//	public Long getEventId() {
//		return eventId;
//	}
//
//	public void setEventId(Long eventId) {
//		this.eventId = eventId;
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
//	public String getReference() {
//		return reference;
//	}
//
//	public void setReference(String reference) {
//		this.reference = reference;
//	}
//
//}

package com.ey.dto.request;

public class PaymentCreateRequest {
    private Long bookingId; // ✅ instead of eventId
    private Double amount;
    private String reference; // optional

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
