package com.ey.dto.response;


import java.time.OffsetDateTime;

public class ReviewResponse {
 private Long id;
 private Long vendorId;
 private Integer rating;
 private String comment;
 private String createdAt;

 // Minimal client info (optional)
 private Long clientId;
 private String clientName;
 private String clientEmail;

 // getters & setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }
 public Long getVendorId() { return vendorId; }
 public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
 public Integer getRating() { return rating; }
 public void setRating(Integer rating) { this.rating = rating; }
 public String getComment() { return comment; }
 public void setComment(String comment) { this.comment = comment; }
 public String      getCreatedAt() { return createdAt; }
 public void setCreatedAt(String string) { this.createdAt = string; }
 public Long getClientId() { return clientId; }
 public void setClientId(Long clientId) { this.clientId = clientId; }
 public String getClientName() { return clientName; }
 public void setClientName(String clientName) { this.clientName = clientName; }
 public String getClientEmail() { return clientEmail; }
 public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
}

