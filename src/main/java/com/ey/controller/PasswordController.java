
package com.ey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ey.dto.request.ForgotPasswordRequest;
import com.ey.dto.request.ResetPasswordRequest;
import com.ey.dto.request.ResetPasswordWithTokenRequest;
import com.ey.service.PasswordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    // 1) Forgot password -> returns reset JWT token OR "Email doesn't exist"
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return passwordService.forgotPassword(request);
    }

    // 2) Reset using reset token
    @PostMapping("reset-token")
    public ResponseEntity<?> resetWithToken(@Valid @RequestBody ResetPasswordWithTokenRequest request) {
        return passwordService.resetWithToken(request);
    }

    // 3) Normal reset when user knows old password
    @PostMapping("/reset")
    public ResponseEntity<?> resetWithOldPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return passwordService.resetWithOldPassword(request);
    }
}

