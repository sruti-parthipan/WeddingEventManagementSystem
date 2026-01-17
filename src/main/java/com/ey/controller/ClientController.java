package com.ey.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.ClientRegistrationRequest;
import com.ey.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ClientRegistrationRequest request) {
        return clientService.createClient(request);
    }

    // No login endpoint here. Login is common at /api/auth/login
}
