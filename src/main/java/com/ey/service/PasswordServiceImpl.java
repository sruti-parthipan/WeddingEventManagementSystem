
package com.ey.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ey.dto.request.ForgotPasswordRequest;
import com.ey.dto.request.ResetPasswordRequest;
import com.ey.dto.request.ResetPasswordWithTokenRequest;
import com.ey.dto.response.ForgotPasswordResponse;
import com.ey.entities.Admin;
import com.ey.entities.Client;
import com.ey.entities.Vendor;
import com.ey.exception.EmailNotFoundException;
import com.ey.exception.InvalidOrExpiredTokenException;
import com.ey.exception.OldPasswordIncorrectException;
import com.ey.exception.PasswordResetException;
import com.ey.exception.TokenEmailMismatchException;
import com.ey.repository.AdminRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.VendorRepository;
import com.ey.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtil jwtUtil;

	private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

	private static class UserRef {
		enum Type {
			ADMIN, CLIENT, VENDOR
		}

		final Type type;
		final Object entity;

		UserRef(Type t, Object e) {
			this.type = t;
			this.entity = e;
		}
	}

	private Optional<UserRef> findUserByEmail(String email) {
		var admin = adminRepository.findByEmail(email).orElse(null);
		if (admin != null)
			return Optional.of(new UserRef(UserRef.Type.ADMIN, admin));
		var client = clientRepository.findByEmail(email).orElse(null);
		if (client != null)
			return Optional.of(new UserRef(UserRef.Type.CLIENT, client));
		var vendor = vendorRepository.findByContactEmail(email).orElse(null); // vendor uses contactEmail
		if (vendor != null)
			return Optional.of(new UserRef(UserRef.Type.VENDOR, vendor));
		return Optional.empty();
	}

	@Override
	public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
		logger.info("Forgot password request started");

		var refOpt = findUserByEmail(request.getEmail());
		if (refOpt.isEmpty()) {
			logger.warn("Email does not exist");
			throw new EmailNotFoundException("Email doesn't exist");
		}

		String resetToken = jwtUtil.generateResetToken(request.getEmail());
		logger.info("Reset token generated");
		return ResponseEntity.ok(new ForgotPasswordResponse(resetToken));
	}

	@Override
	@Transactional
	public ResponseEntity<?> resetWithToken(ResetPasswordWithTokenRequest request) {
		logger.info("Reset password with token request started");

		if (!jwtUtil.validateResetToken(request.getToken())) {
			logger.warn("Invalid or expired token");
			throw new InvalidOrExpiredTokenException("Invalid or expired token");
		}

		String tokenEmail = jwtUtil.extractEmailFromResetToken(request.getToken());
		if (!tokenEmail.equalsIgnoreCase(request.getEmail())) {
			logger.warn("Token-email mismatch");
			throw new TokenEmailMismatchException("Token-email mismatch");
		}

		var refOpt = findUserByEmail(request.getEmail());
		if (refOpt.isEmpty()) {
			logger.warn("Email does not exist");
			throw new EmailNotFoundException("Email doesn't exist");
		}

		try {
			String encoded = passwordEncoder.encode(request.getNewPassword());
			var ref = refOpt.get();
			switch (ref.type) {
			case ADMIN -> {
				Admin a = (Admin) ref.entity;
				a.setPassword(encoded);
				adminRepository.save(a);
			}
			case CLIENT -> {
				Client c = (Client) ref.entity;
				c.setPassword(encoded);
				clientRepository.save(c);
			}
			case VENDOR -> {
				Vendor v = (Vendor) ref.entity;
				v.setPassword(encoded);
				vendorRepository.save(v);
			}
			}
			logger.info("Password reset successful");
			return ResponseEntity.ok("Password reset successful");
		} catch (Exception ex) {
			logger.error("Password reset failed", ex);
			throw new PasswordResetException("Password reset failed");
		}
	}

	@Override
	@Transactional
	public ResponseEntity<?> resetWithOldPassword(ResetPasswordRequest request) {
		logger.info("Reset password with old password request started");

		var refOpt = findUserByEmail(request.getEmailId());
		if (refOpt.isEmpty()) {
			logger.warn("Email does not exist");
			throw new EmailNotFoundException("Email doesn't exist");
		}

		var ref = refOpt.get();
		String encodedNew = passwordEncoder.encode(request.getNewPassword());

		switch (ref.type) {
		case ADMIN -> {
			Admin a = (Admin) ref.entity;
			if (!passwordEncoder.matches(request.getOldPassword(), a.getPassword())) {
				logger.warn("Old password incorrect");
				throw new OldPasswordIncorrectException("Old password incorrect");
			}
			a.setPassword(encodedNew);
			adminRepository.save(a);
		}
		case CLIENT -> {
			Client c = (Client) ref.entity;
			if (!passwordEncoder.matches(request.getOldPassword(), c.getPassword())) {
				logger.warn("Old password incorrect");
				throw new OldPasswordIncorrectException("Old password incorrect");
			}
			c.setPassword(encodedNew);
			clientRepository.save(c);
		}
		case VENDOR -> {
			Vendor v = (Vendor) ref.entity;
			if (!passwordEncoder.matches(request.getOldPassword(), v.getPassword())) {
				logger.warn("Old password incorrect");
				throw new OldPasswordIncorrectException("Old password incorrect");
			}
			v.setPassword(encodedNew);
			vendorRepository.save(v);
		}
		}

		logger.info("Password changed successfully");
		return ResponseEntity.ok("Password changed successfully");
	}
}
