package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.service.ClientViewVendorService;

@RestController
@RequestMapping("/api/client/vendors")
public class ClientViewVendorController {
@Autowired
private ClientViewVendorService clientViewVendorService;
@GetMapping("/view")
public ResponseEntity<?> getAllVendors() {
    return clientViewVendorService.getAllVendors();
}

//Get vendor by ID
 @GetMapping("/{id}")
 public ResponseEntity<?> getVendorById(@PathVariable Long id) {
     return clientViewVendorService.getVendorById(id);
 }

//Get vendor by Name
  @GetMapping("/by-name/{name}")
  public ResponseEntity<?> getVendorByName(@PathVariable String name) {
      return clientViewVendorService.getVendorByName(name);
  }

  // Get vendor by Email
  @GetMapping("/by-email/{email}")
  public ResponseEntity<?> getVendorByEmail(@PathVariable String email) {
      return clientViewVendorService.getVendorByEmail(email);
  }

//Get vendor by Contact Phone
   @GetMapping("/by-phone/{phone}")
   public ResponseEntity<?> getVendorByContactPhone(@PathVariable String phone) {
       return clientViewVendorService.getVendorByContactPhone(phone);
   }

// Get vendors by ServiceType (enum)
// Example: GET /api/client/vendors/by-service-type?serviceType=PHOTOGRAPHY
@GetMapping("/by-service-type/{serviceType}")
public ResponseEntity<?> getVendorsByServiceType(@PathVariable String serviceType) {
    return clientViewVendorService.getVendorsByServiceType(serviceType);
}


   // Get vendors with basePrice > amount
   @GetMapping("/price/greaterthan/{amount}") 
   public ResponseEntity<?> getVendorsByBasePriceGreaterThan(@PathVariable Double amount) {
       return clientViewVendorService.getVendorsByBasePriceGreaterThan(amount);
   }

// Get vendors with basePrice < amount
    @GetMapping("/price/lesserthan/{amount}")
    public ResponseEntity<?> getVendorsByBasePriceLessThan(@PathVariable Double amount) {
        return clientViewVendorService.getVendorsByBasePriceLessThan(amount);
    }

    // Get vendors with min <= basePrice <= max
    @GetMapping("/price/between/{min}/{max}")
    public ResponseEntity<?> getVendorsByBasePriceBetween(@PathVariable Double min,
                                                          @PathVariable Double max) {
        return clientViewVendorService.getVendorsByBasePriceBetween(min, max);
    }

@GetMapping("/by/{serviceType}/{basePrice}")
public ResponseEntity<?> getVendorsByServiceTypeAndBasePrice(@PathVariable String serviceType,
                                                             @PathVariable Double basePrice) {
    return clientViewVendorService.getVendorsByServiceTypeAndBasePrice(serviceType, basePrice);
}
@GetMapping("/{vendorId}/reviews")
public ResponseEntity<?> listReviews(@PathVariable Long vendorId, Authentication auth) {
    String email = auth.getName();
    return clientViewVendorService.listReviewsForVendor(vendorId, email);
}

// Get compact ratings list + aggregates for a vendor
@GetMapping("/{vendorId}/ratings")
public ResponseEntity<?> getRatings(@PathVariable Long vendorId, Authentication auth) {
    String email = auth.getName();
    return clientViewVendorService.getRatingsForVendor(vendorId, email);
}

@GetMapping("/reviews")
    public ResponseEntity<?> listAllVendorReviews(Authentication auth) {
        String email = auth.getName();
        return clientViewVendorService.listAllVendorReviews(email);
    }



}
