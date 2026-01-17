package com.ey.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ey.dto.request.ClientRegistrationRequest;
import com.ey.dto.response.ClientRegistrationResponse;
import com.ey.entities.Client;
import com.ey.enums.Role;
import com.ey.repository.ClientRepository;
import com.ey.security.JwtUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
@Service
public class ClientServiceImpl implements ClientService {
	@Autowired
	private ClientRepository clientRepository;
		@Autowired
	    private  PasswordEncoder passwordEncoder;
		@Autowired
	    private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;
	    @Override
	    @Transactional
	
	public ResponseEntity<?> createClient(@Valid ClientRegistrationRequest request) {
		// TODO Auto-generated method stub
		 if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
	            return ResponseEntity.badRequest().body("Email already exists");
	        }


Client c = new Client();
        c.setName(request.getName());
        c.setEmail(request.getEmail());
        c.setPassword(passwordEncoder.encode(request.getPassword()));
        c.setPhone(request.getPhone());
        c.setAddress(request.getAddress());
        c.setRole(Role.CLIENT);

        Client saved = clientRepository.save(c);

ClientRegistrationResponse resp = new ClientRegistrationResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());
        resp.setEmail(saved.getEmail());
        resp.setRole(saved.getRole()); // enum
        resp.setPhone(saved.getPhone());
        resp.setPassword(saved.getPassword());
        resp.setAddress(saved.getAddress());
        resp.setCreatedAt(saved.getCreatedAt() != null
                ? saved.getCreatedAt().formatted(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        resp.setUpdatedAt(saved.getUpdatedAt() != null
                ? saved.getUpdatedAt().formatted(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);

        // If your ClientRegistrationResponse uses String timestamps:
       // resp.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().format(ISO) : null);
       // resp.setUpdatedAt(saved.getUpdatedAt() != null ? saved.getUpdatedAt().format(ISO) : null);

        return ResponseEntity.ok(resp);

	}

}
