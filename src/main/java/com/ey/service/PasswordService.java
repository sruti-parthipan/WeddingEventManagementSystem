package com.ey.service;

import org.springframework.http.ResponseEntity;

import com.ey.dto.request.ForgotPasswordRequest;
import com.ey.dto.request.ResetPasswordRequest;
import com.ey.dto.request.ResetPasswordWithTokenRequest;

public interface PasswordService {

	ResponseEntity<?> forgotPassword(ForgotPasswordRequest request);

	ResponseEntity<?> resetWithToken(ResetPasswordWithTokenRequest request);

	ResponseEntity<?> resetWithOldPassword(ResetPasswordRequest request);

}
