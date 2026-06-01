package in.guvi.task.springbootmvc.exception;

import in.guvi.task.springbootmvc.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

/**
 * Global Exception Interceptor for the application.
 * Uses @RestControllerAdvice to catch exceptions thrown by any controller across the application
 * and translates them into standardized, user-friendly JSON HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // SLF4J Logger to record error details in the server console/logs for debugging
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepts custom ResourceNotFoundException.
     * Triggered when a requested entity (e.g., an Employee ID) does not exist in the database.
     *
     * @param ex The intercepted ResourceNotFoundException.
     * @return Standardized ErrorResponse with a 404 (NOT_FOUND) status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Resource Not Found");
    }

    /**
     * Intercepts database constraint violations.
     * Triggered when attempting to save duplicate unique data (like an email) or breaking foreign keys.
     *
     * @param ex The intercepted DataIntegrityViolationException.
     * @return Standardized ErrorResponse with a 409 (CONFLICT) status.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDatabaseConstraintViolation(DataIntegrityViolationException ex) {
        logger.error("Database constraint violation: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, "Database conflict: Likely a duplicate entry or missing foreign key.");
    }

    /**
     * Intercepts validation errors from @Valid / @Validated annotations.
     * Triggered when the incoming JSON payload fails constraints (e.g., missing @NotNull fields).
     *
     * @param ex The intercepted MethodArgumentNotValidException.
     * @return Standardized ErrorResponse with a 400 (BAD_REQUEST) status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        // Extracts the specific field error message (e.g., "Email cannot be blank")
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildErrorResponse(new RuntimeException(errorMessage), HttpStatus.BAD_REQUEST, "Validation Failed");
    }

    /**
     * Intercepts type mismatch errors in the URI.
     * Triggered when a client passes the wrong data type in a @PathVariable (e.g., passing a String when an Integer is expected).
     *
     * @param ex The intercepted MethodArgumentTypeMismatchException.
     * @return Standardized ErrorResponse with a 400 (BAD_REQUEST) status.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Invalid parameter type in URL.");
    }

    /**
     * Catch-All Handler for any unexpected runtime or system exceptions.
     * Acts as a safety net to prevent raw server stack traces from leaking to the client,
     * which is a critical security best practice.
     *
     * @param ex The generic Exception intercepted.
     * @return Standardized ErrorResponse with a generic message and a 500 (INTERNAL_SERVER_ERROR) status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        // Log the full stack trace internally for developers to debug
        logger.error("Unexpected System Error occurred", ex);

        // Return a sanitized response to the external client
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected internal server error occurred. Please contact support.")
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to construct the ErrorResponse object.
     * Promotes the DRY (Don't Repeat Yourself) principle by centralizing the builder logic.
     *
     * @param ex The exception containing the error message.
     * @param status The HTTP status code to return.
     * @param customErrorTitle A high-level category or title for the error.
     * @return A fully populated ResponseEntity.
     */
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(Exception ex, HttpStatus status, String customErrorTitle) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(customErrorTitle)
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDto, status);
    }
}
