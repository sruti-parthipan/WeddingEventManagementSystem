

package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ey.dto.request.VendorRegistrationRequest;
import com.ey.service.VendorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody VendorRegistrationRequest request) {
        return vendorService.createVendor(request);
    }

    // No login endpoint here. Use POST /api/auth/login for all roles.
}
