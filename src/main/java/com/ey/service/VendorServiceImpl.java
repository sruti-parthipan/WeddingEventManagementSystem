package com.ey.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ey.dto.request.VendorRegistrationRequest;
import com.ey.dto.response.VendorRegistrationResponse;
import com.ey.entities.Vendor;
import com.ey.enums.Role;
import com.ey.repository.VendorRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
@Service
public class VendorServiceImpl implements VendorService{

@Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public ResponseEntity<?> createVendor(@Valid VendorRegistrationRequest request) {
		// TODO Auto-generated method stub

		// Assuming vendors use contactEmail as unique email
		        if (vendorRepository.findByContactEmail(request.getContactEmail()).isPresent()) {
		            return ResponseEntity.badRequest().body("Email already exists");
		        }

		        Vendor v = new Vendor();
		        v.setName(request.getName());
		        v.setServiceType(request.getServiceType());
		        v.setContactEmail(request.getContactEmail());
		        v.setContactPhone(request.getContactPhone());
		        v.setBasePrice(request.getBasePrice());
		        v.setPassword(passwordEncoder.encode(request.getPassword())); // store hashed
		        v.setRole(Role.VENDOR);

Vendor saved = vendorRepository.save(v);

        VendorRegistrationResponse resp = new VendorRegistrationResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());
        resp.setContactEmail(saved.getContactEmail());
        resp.setContactPhone(saved.getContactPhone());
        resp.setServiceType(saved.getServiceType());
        resp.setBasePrice(saved.getBasePrice());
        resp.setRole(saved.getRole());
        resp.setCreatedAt(saved.getCreatedAt() != null
                ? saved.getCreatedAt().formatted(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        resp.setUpdatedAt(saved.getUpdatedAt() != null
                ? saved.getUpdatedAt().formatted(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
		return ResponseEntity.ok(resp);
	}

}
