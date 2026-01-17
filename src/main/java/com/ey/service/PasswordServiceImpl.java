

package com.ey.service;

import java.util.Optional;

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
import com.ey.repository.AdminRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.VendorRepository;
import com.ey.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired private AdminRepository adminRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private VendorRepository vendorRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    /** Find user by email across Admin/Client/Vendor. Returns object + a type flag. */
    private static class UserRef {
        enum Type { ADMIN, CLIENT, VENDOR }
        final Type type;
        final Object entity;
        UserRef(Type t, Object e) { this.type = t; this.entity = e; }
    }

    private Optional<UserRef> findUserByEmail(String email) {
        var admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) return Optional.of(new UserRef(UserRef.Type.ADMIN, admin));
        var client = clientRepository.findByEmail(email).orElse(null);
        if (client != null) return Optional.of(new UserRef(UserRef.Type.CLIENT, client));
        var vendor = vendorRepository.findByContactEmail(email).orElse(null); // vendor uses contactEmail
        if (vendor != null) return Optional.of(new UserRef(UserRef.Type.VENDOR, vendor));
        return Optional.empty();
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        var refOpt = findUserByEmail(email);
        if (refOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email doesn't exist");
        }
        String resetToken = jwtUtil.generateResetToken(email); // short-lived reset token
        return ResponseEntity.ok(new ForgotPasswordResponse(resetToken));
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetWithToken(ResetPasswordWithTokenRequest request) {
        String token = request.getToken();
        if (!jwtUtil.validateResetToken(token)) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        String tokenEmail = jwtUtil.extractEmailFromResetToken(token);
        if (!tokenEmail.equalsIgnoreCase(request.getEmail())) {
            return ResponseEntity.badRequest().body("Token-email mismatch");
        }

        var refOpt = findUserByEmail(request.getEmail());
        if (refOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email doesn't exist");
        }

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
        return ResponseEntity.ok("Password reset successful");
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetWithOldPassword(ResetPasswordRequest request) {
        String email = request.getEmailId();
        var refOpt = findUserByEmail(email);
        if (refOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email doesn't exist");
        }

        var ref = refOpt.get();
        String encodedNew = passwordEncoder.encode(request.getNewPassword());

        switch (ref.type) {
            case ADMIN -> {
                Admin a = (Admin) ref.entity;
                if (!passwordEncoder.matches(request.getOldPassword(), a.getPassword())) {
                    return ResponseEntity.badRequest().body("Old password incorrect");
                }
                a.setPassword(encodedNew);
                adminRepository.save(a);
            }
            case CLIENT -> {
                Client c = (Client) ref.entity;
                if (!passwordEncoder.matches(request.getOldPassword(), c.getPassword())) {
                    return ResponseEntity.badRequest().body("Old password incorrect");
                }
                c.setPassword(encodedNew);
                clientRepository.save(c);
            }
            case VENDOR -> {
                Vendor v = (Vendor) ref.entity;
                if (!passwordEncoder.matches(request.getOldPassword(), v.getPassword())) {
                    return ResponseEntity.badRequest().body("Old password incorrect");
                }
                v.setPassword(encodedNew);
                vendorRepository.save(v);
            }
        }

        return ResponseEntity.ok("Password changed successfully");
    }
}
