package in.guvi.task.springbootmvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a standardized error response payload.
 *
 * <p>This DTO is returned as a JSON body by the {@link in.guvi.task.springbootmvc.exception.GlobalExceptionHandler}
 * whenever an exception is caught. It provides a consistent, structured error format
 * for the client/consumer, avoiding raw stack trace leakage.
 *
 * <p>Fields included in every error response:
 * <ul>
 *   <li>{@code timestamp} — exact time the error occurred (useful for log correlation)</li>
 *   <li>{@code status}    — HTTP status code (e.g., 404, 400, 500)</li>
 *   <li>{@code error}     — a short human-readable error title (e.g., "Resource Not Found")</li>
 *   <li>{@code message}   — a detailed description of the specific error</li>
 * </ul>
 *
 * <p>Lombok annotations:
 * <ul>
 *   <li>{@code @Data}   - generates getters, setters, equals, hashCode, toString</li>
 *   <li>{@code @Builder} - enables fluent construction: {@code ErrorResponseDto.builder().status(404)...build()}</li>
 *   <li>{@code @AllArgsConstructor} - all-fields constructor (required alongside @Builder)</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDto {

    /** The server timestamp at the moment the exception was caught and the response was built. */
    private LocalDateTime timestamp;

    /** The HTTP status code as an integer (e.g., 400, 404, 409, 500). */
    private Integer status;

    /** A short, human-readable title categorizing the error (e.g., "Validation Failed"). */
    private String error;

    /** A detailed message describing exactly what went wrong (sourced from the exception's message). */
    private String message;
}
