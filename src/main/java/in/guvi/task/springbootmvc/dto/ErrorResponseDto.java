package in.guvi.task.springbootmvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standardized custom error response template.
 * This class ensures that all API exceptions return a consistent JSON structure to the client,
 * masking internal stack traces while providing clear, actionable error details.
 */
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically
@Builder // Implements the Builder design pattern for clean object creation in the exception handler
@AllArgsConstructor // Generates a constructor with all fields, required by the @Builder annotation
public class ErrorResponseDto {

    /**
     * The exact date and time when the error occurred.
     * Useful for tracing issues in server logs.
     */
    private LocalDateTime timestamp;

    /**
     * The HTTP status code (e.g., 400, 404, 500) indicating the type of failure.
     */
    private Integer status;

    /**
     * A brief, high-level description of the HTTP status (e.g., "Bad Request", "Not Found").
     */
    private String error;

    /**
     * A specific, user-friendly message explaining exactly what went wrong
     * (e.g., "Employee not found with id: EMP001").
     */
    private String message;
}
