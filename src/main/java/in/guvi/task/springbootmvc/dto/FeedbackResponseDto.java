package in.guvi.task.springbootmvc.dto;

import in.guvi.task.springbootmvc.validations.annotations.ValidName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for outgoing feedback data sent to the view layer.
 *
 * <p>Unlike {@link FeedbackRequestDto} (used for form input), this DTO is used when
 * the service layer <em>returns</em> feedback records to the controller and view.
 * It includes the {@code id} field so the view can generate edit/delete links
 * with the correct resource identifier (e.g., {@code /feedback/edit/5}).
 *
 * <p>The validation annotations on the fields are technically not required for a response DTO
 * but serve as a contract/documentation of what values are expected to be present.
 *
 * <p>Lombok annotations:
 * <ul>
 *   <li>{@code @Data}            - generates getters, setters, equals, hashCode, and toString</li>
 *   <li>{@code @AllArgsConstructor} - all-fields constructor (used by the builder)</li>
 *   <li>{@code @NoArgsConstructor}  - no-args constructor</li>
 *   <li>{@code @Builder}         - fluent builder used in the service layer to map entities → DTOs</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponseDto {

    /**
     * The unique database-assigned ID of the feedback record.
     * Used by Thymeleaf templates to construct dynamic edit/delete action URLs.
     */
    @NotNull(message = "ID can't be null")
    private Long id;

    /** The name of the reader. Must be present, non-blank, and contain only letters and spaces. */
    @NotNull(message = "Reader name can't be null")
    @NotEmpty(message = "Reader name can't be empty")
    @NotBlank(message = "Reader name cannot consist of only empty spaces")
    @ValidName(message = "Reader name must contain only letters and spaces")
    private String name;

    /** The title of the book the feedback relates to. Must be present and non-blank. */
    @NotNull(message = "Book name can't be null")
    @NotEmpty(message = "Book name can't be empty")
    @NotBlank(message = "Book name cannot consist of only empty spaces")
    private String bookName;

    /** The feedback/review content. Must be present and non-blank. */
    @NotNull(message = "Feedback can't be null")
    @NotEmpty(message = "Feedback can't be empty")
    @NotBlank(message = "Feedback cannot consist of only empty spaces")
    private String feedback;
}
