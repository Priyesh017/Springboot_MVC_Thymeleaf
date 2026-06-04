package in.guvi.task.springbootmvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for incoming feedback form submissions.
 *
 * <p>This DTO acts as a clean boundary between the HTTP layer (HTML form) and the
 * domain/service layer. It carries only the data a user needs to <em>submit</em>
 * (no ID, no auto-generated fields), keeping the API surface minimal and safe.
 *
 * <p>Bean Validation annotations ({@code @NotNull}, {@code @NotEmpty}, {@code @NotBlank})
 * are applied to each field. These are enforced by Spring MVC when the controller
 * uses {@code @Valid} on the method parameter. If any constraint fails, a
 * {@link org.springframework.validation.BindingResult} captures the errors.
 *
 * <p>Lombok annotations:
 * <ul>
 *   <li>{@code @Data}            - generates getters, setters, equals, hashCode, and toString</li>
 *   <li>{@code @AllArgsConstructor} - all-fields constructor</li>
 *   <li>{@code @NoArgsConstructor}  - no-args constructor (required by Thymeleaf model binding)</li>
 *   <li>{@code @Builder}         - enables fluent builder (useful in service/test code)</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequestDto {

    /**
     * The name of the reader submitting the feedback.
     * <ul>
     *   <li>{@code @NotNull}  — field must not be absent (null)</li>
     *   <li>{@code @NotEmpty} — field must not be an empty string ""</li>
     *   <li>{@code @NotBlank} — field must not consist of only whitespace characters</li>
     * </ul>
     */
    @NotNull(message = "Reader name can't be null")
    @NotEmpty(message = "Reader name can't be empty")
    @NotBlank(message = "Reader name cannot consist of only empty spaces")
    private String name;

    /**
     * The title of the book the feedback is written for.
     * Same layered validation: must be non-null, non-empty, and non-blank.
     */
    @NotNull(message = "Book name can't be null")
    @NotEmpty(message = "Book name can't be empty")
    @NotBlank(message = "Book name cannot consist of only empty spaces")
    private String bookName;

    /**
     * The actual feedback/review content written by the reader.
     * Same layered validation: must be non-null, non-empty, and non-blank.
     */
    @NotNull(message = "Feedback can't be null")
    @NotEmpty(message = "Feedback can't be empty")
    @NotBlank(message = "Feedback cannot consist of only empty spaces")
    private String feedback;
}
