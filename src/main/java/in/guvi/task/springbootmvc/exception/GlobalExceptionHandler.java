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
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

/**
 * Global Exception Interceptor for the entire application.
 *
 * <p>{@code @RestControllerAdvice} is a specialization of {@code @ControllerAdvice} that applies
 * across ALL controllers in the application. It intercepts exceptions thrown anywhere in the
 * controller or service layers and converts them into structured, standardized JSON HTTP responses
 * using the {@link ErrorResponseDto} format.
 *
 * <p>This centralized approach avoids duplicating try-catch blocks in every controller method
 * and ensures clients always receive a consistent error payload (timestamp, status, error, message).
 *
 * <p>Each handler method is annotated with {@code @ExceptionHandler(SomeException.class)},
 * telling Spring to invoke that method whenever the specified exception type is thrown.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * SLF4J Logger used to write exception details to the server console/log files.
     * Always log internally before returning a sanitized response to the client —
     * the client should never see raw stack traces for security reasons.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles the custom {@link ResourceNotFoundException}.
     *
     * <p>Triggered when a service method calls {@code repository.findById(id).orElseThrow(...)},
     * and no matching record is found in the database (e.g., editing or deleting a non-existent product).
     *
     * <p>HTTP response: {@code 404 NOT FOUND}
     *
     * @param ex the intercepted {@link ResourceNotFoundException} carrying the "not found" message
     * @return a {@link ResponseEntity} containing an {@link ErrorResponseDto} with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Resource Not Found");
    }

    /**
     * Handles database-level constraint violations.
     *
     * <p>Triggered when Spring Data JPA / Hibernate attempts to execute an INSERT or UPDATE
     * that violates a database constraint (e.g., duplicate entry for a UNIQUE column,
     * or a missing foreign key reference).
     *
     * <p>HTTP response: {@code 409 CONFLICT}
     *
     * @param ex the intercepted {@link DataIntegrityViolationException}
     * @return a {@link ResponseEntity} with a 409 status and a conflict message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDatabaseConstraintViolation(DataIntegrityViolationException ex) {
        logger.error("Database constraint violation: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, "Database conflict: Likely a duplicate entry or missing foreign key.");
    }

    /**
     * Handles Bean Validation failures triggered by {@code @Valid} on controller method parameters.
     *
     * <p>When any constraint (e.g., {@code @NotNull}, {@code @NotBlank}, {@code @Min}) fails
     * on an incoming request body or form DTO, Spring MVC throws a
     * {@link MethodArgumentNotValidException}. This handler extracts the first field-level
     * error message and returns it to the client.
     *
     * <p>HTTP response: {@code 400 BAD REQUEST}
     *
     * @param ex the intercepted {@link MethodArgumentNotValidException} containing field errors
     * @return a {@link ResponseEntity} with a 400 status and the first validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        // Extract only the first field-level violation message (e.g., "Price cannot be negative")
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildErrorResponse(new RuntimeException(errorMessage), HttpStatus.BAD_REQUEST, "Validation Failed");
    }

    /**
     * Handles type mismatches in URL path variables or request parameters.
     *
     * <p>Triggered when a client provides a value of the wrong type in a {@code @PathVariable}
     * or {@code @RequestParam} — for example, passing the string "abc" where a {@code Long} ID is expected:
     * {@code GET /product/edit/abc} would trigger this handler.
     *
     * <p>HTTP response: {@code 400 BAD REQUEST}
     *
     * @param ex the intercepted {@link MethodArgumentTypeMismatchException}
     * @return a {@link ResponseEntity} with a 400 status and an invalid-parameter-type message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Invalid parameter type in URL.");
    }

    /**
     * Catch-all fallback handler for any unexpected exception not covered by the handlers above.
     *
     * <p>Acts as a final safety net to prevent raw Java stack traces from leaking in the
     * HTTP response (a critical security practice). The full exception is logged internally
     * for debugging, but only a generic message is returned to the client.
     *
     * <p>HTTP response: {@code 500 INTERNAL SERVER ERROR}
     *
     * @param ex any unhandled {@link Exception} thrown within the application
     * @return a {@link ResponseEntity} with a 500 status and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {
        // Log the full stack trace internally for developers to debug
        logger.error("Unexpected System Error occurred", ex);

        // Return a sanitized, generic response to the external client (no stack trace exposed)
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected internal server error occurred. Please contact support.")
                .build();

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles requests to URLs that have no matching controller handler.
     *
     * <p>Intercepting {@link NoHandlerFoundException} silences browser-generated requests
     * like {@code GET /favicon.ico} and prevents them from bubbling up to the generic
     * 500-handler. Returns a plain 404 response body instead.
     *
     * @param ex the intercepted {@link NoHandlerFoundException}
     * @return a {@link ResponseEntity} with 404 status and a plain "Resource not found" message
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFoundError(NoHandlerFoundException ex) {
        // Silently intercepts unmatched URLs (e.g., /favicon.ico) with a clean 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }

    /**
     * Shared helper method that constructs a standardized {@link ErrorResponseDto} response.
     *
     * <p>Promotes the DRY (Don't Repeat Yourself) principle — all error-building logic
     * is centralized here. Each specific handler delegates to this method to avoid
     * duplicating the builder block.
     *
     * @param ex               the exception whose message will populate the {@code message} field
     * @param status           the HTTP status code to apply (e.g., 404, 400, 409)
     * @param customErrorTitle a short, human-readable title for the error category
     * @return a fully constructed {@link ResponseEntity} ready to be returned from the handler
     */
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(Exception ex, HttpStatus status, String customErrorTitle) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())    // Capture the exact time of the error
                .status(status.value())            // e.g., 404, 400, 409
                .error(customErrorTitle)           // Human-readable error category
                .message(ex.getMessage())          // Specific exception detail message
                .build();

        return new ResponseEntity<>(errorResponseDto, status);
    }
}
