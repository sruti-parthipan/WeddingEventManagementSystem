package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.response.MessageResponse;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

	  @PostMapping("/logout")
	    public ResponseEntity<MessageResponse> logout() {
	        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
	    }
 
}
 