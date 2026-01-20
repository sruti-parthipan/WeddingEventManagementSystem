
package com.ey.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ey.config.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(AuthenticationManager authManager, JwtUtil jwtUtil) {
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/auth/login"); // Custom login endpoint
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginBody creds = new ObjectMapper().readValue(request.getInputStream(), LoginBody.class);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(),
					creds.getPassword());

			return authManager.authenticate(authToken); // Spring will call unsuccessfulAuthentication() if wrong
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");

		Map<String, String> error = new HashMap<>();
		error.put("message", "Invalid email or password");

		new ObjectMapper().writeValue(response.getWriter(), error);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException {
		// Get authenticated user details
		UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();

		// Generate JWT token using email
		String token = jwtUtil.generateToken(principal.getUsername());

		// Build response body
		Map<String, Object> body = new HashMap<>();
		body.put("token", token);
		body.put("userId", principal.getId());
		body.put("name", principal.getDisplayName());
		body.put("email", principal.getUsername());
		body.put("role", principal.getAppRole().name());

		// Send JSON response
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getWriter(), body);
	}

	// Inner class for login request payload
	public static class LoginBody {
		private String email;
		private String password;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
