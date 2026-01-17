package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.ClientRegistrationRequest;

import jakarta.validation.Valid;

public interface ClientService {

	

	ResponseEntity<?> createClient(@Valid ClientRegistrationRequest request);

}
