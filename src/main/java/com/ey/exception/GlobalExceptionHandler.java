
package com.ey.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
* Global exception handler that returns simple messages only (no IDs/emails).
* Validation errors include a small JSON with a field error map.
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

 /* ---------- Utility: simple message body ---------- */
 private ResponseEntity<Object> msg(HttpStatus status, String message) {
     return ResponseEntity.status(status).body(message);
 }

 /* ========== AUTH / LOGIN ========== */

 @ExceptionHandler(AuthenticationFailedException.class)
 public ResponseEntity<Object> handleAuthFailed(AuthenticationFailedException ex, HttpServletRequest req) {
     logger.warn("Authentication failed");
     return msg(HttpStatus.UNAUTHORIZED, ex.getMessage()); // 401
 }

 @ExceptionHandler(AdminLoginException.class)
 public ResponseEntity<Object> handleAdminLogin(AdminLoginException ex, HttpServletRequest req) {
     logger.error("Admin login error");
     return msg(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()); // 500
 }

 // Spring Security specifics (if they bubble up)
 @ExceptionHandler(BadCredentialsException.class)
 public ResponseEntity<Object> handleBadCreds(BadCredentialsException ex, HttpServletRequest req) {
     logger.warn("Bad credentials");
     return msg(HttpStatus.UNAUTHORIZED, "Invalid email or password");
 }

 @ExceptionHandler(DisabledException.class)
 public ResponseEntity<Object> handleDisabled(DisabledException ex, HttpServletRequest req) {
     logger.warn("Account disabled");
     return msg(HttpStatus.FORBIDDEN, "Account disabled. Contact support");
 }

 @ExceptionHandler(LockedException.class)
 public ResponseEntity<Object> handleLocked(LockedException ex, HttpServletRequest req) {
     logger.warn("Account locked");
     return msg(HttpStatus.FORBIDDEN, "Account locked. Contact support");
 }

 /* ========== REGISTRATION / CREATION / UPDATE ERRORS ========== */

 @ExceptionHandler(EmailAlreadyExistsException.class)
 public ResponseEntity<Object> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest req) {
     logger.warn("Email already exists");
     return msg(HttpStatus.BAD_REQUEST, ex.getMessage()); // 400
 }

 @ExceptionHandler({
         ClientCreationException.class,
         AdminCreationException.class,
         VendorCreationException.class,
         VendorFetchException.class,
         EventCreateException.class,
         EventUpdateException.class,
         EventDeleteException.class,
         BookingCreationException.class,
         ReviewCreationException.class,
         PaymentConfirmationException.class,
         PasswordResetException.class
 })
 public ResponseEntity<Object> handleCreateUpdateErrors(RuntimeException ex, HttpServletRequest req) {
     logger.error("Server error");
     return msg(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()); // 500
 }

 /* ========== UNAUTHORIZED / FORBIDDEN ========== */

 @ExceptionHandler({
         ClientUnauthorizedException.class,
         VendorUnauthorizedException.class
 })
 public ResponseEntity<Object> handleUnauthorized(RuntimeException ex, HttpServletRequest req) {
     logger.warn("Unauthorized");
     return msg(HttpStatus.UNAUTHORIZED, ex.getMessage()); // 401
 }

 @ExceptionHandler(NotAuthorizedException.class)
 public ResponseEntity<Object> handleForbidden(NotAuthorizedException ex, HttpServletRequest req) {
     logger.warn("Forbidden");
     return msg(HttpStatus.FORBIDDEN, ex.getMessage()); // 403
 }

 /* ========== NOT FOUND ========== */

 @ExceptionHandler({
         ClientNotFoundException.class,
         VendorNotFoundException.class,
         EventNotFoundException.class,
         BookingNotFoundException.class,
         PaymentNotFoundException.class
 })
 public ResponseEntity<Object> handleNotFound(RuntimeException ex, HttpServletRequest req) {
     logger.warn("Resource not found");
     return msg(HttpStatus.NOT_FOUND, ex.getMessage()); // 404
 }

 // For list/collection empties
 @ExceptionHandler({
         NoClientsFoundException.class,
         NoVendorsFoundException.class,
         NoEventsFoundException.class,
         NoReviewsFoundException.class,
         NoRatingsFoundException.class
 })
 public ResponseEntity<Object> handleNoItems(RuntimeException ex, HttpServletRequest req) {
     logger.warn("No items found");
     return msg(HttpStatus.NOT_FOUND, ex.getMessage()); // 404
 }

 /* ========== BAD REQUEST / BUSINESS RULES ========== */

 @ExceptionHandler({
         InvalidStatusException.class,
         InvalidServiceTypeException.class,
         InvalidReviewRequestException.class,
         BookingNotCompletedException.class,
         DuplicateBookingException.class,
         PaymentAlreadyProcessedException.class,
         OldPasswordIncorrectException.class,
         InvalidOrExpiredTokenException.class,
         TokenEmailMismatchException.class,
         EmailNotFoundException.class
 })
 public ResponseEntity<Object> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
     logger.warn("Bad request");
     return msg(HttpStatus.BAD_REQUEST, ex.getMessage()); // 400
 }

 /* ========== VALIDATION (DTO, Params) ========== */

 // @Valid on @RequestBody DTOs
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest req) {
     logger.warn("Validation failed");
     Map<String, Object> body = new HashMap<>();
     body.put("message", "Validation failed");

     Map<String, String> errors = new HashMap<>();
     ex.getBindingResult().getAllErrors().forEach(err -> {
         String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
         errors.put(field, err.getDefaultMessage());
     });
     body.put("errors", errors);

     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // 400
 }

 // Constraint validations on @RequestParam / @PathVariable, etc.
 @ExceptionHandler(ConstraintViolationException.class)
 public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
     logger.warn("Constraint violation");
     Map<String, Object> body = new HashMap<>();
     body.put("message", "Validation failed");

     Map<String, String> errors = new HashMap<>();
     ex.getConstraintViolations().forEach(v ->
             errors.put(v.getPropertyPath().toString(), v.getMessage()));
     body.put("errors", errors);

     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // 400
 }

 /* ========== COMMON SPRING DATA ERROR (optional safety) ========== */

 @ExceptionHandler(DataIntegrityViolationException.class)
 public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
     logger.warn("Data integrity violation");
     return msg(HttpStatus.BAD_REQUEST, "Data integrity violation"); // 400
 }

 /* ========== FALLBACK ========== */

 @ExceptionHandler(Exception.class)
 public ResponseEntity<Object> handleOther(Exception ex, HttpServletRequest req) {
     logger.error("Unexpected error", ex);
     return msg(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"); // 500
 
}
}
