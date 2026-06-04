package in.guvi.task.springbootmvc.dto;

import in.guvi.task.springbootmvc.validations.annotations.ValidName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for outgoing product data sent to the view/template layer.
 *
 * <p>Used by the service layer to return product records to the controller, and then
 * to Thymeleaf templates for rendering in the product catalog. This DTO includes the
 * {@code id} field (unlike {@link ProductRequestDto}) because the view needs it to
 * construct edit and delete action URLs dynamically.
 *
 * <p>The {@code @NoArgsConstructor} is intentionally omitted here because the {@code id}
 * field is {@code final}, and this DTO is always constructed via the builder in the service layer.
 *
 * <p>The custom {@link ValidName} annotation is applied to {@code productName} to reject
 * inputs containing digits or special characters (e.g., {@code "TV@4K"} or {@code "Laptop2"}),
 * ensuring only semantically valid product names are stored.
 *
 * <p>Lombok annotations used:
 * <ul>
 *   <li>{@code @Data}            - generates getters, setters, equals, hashCode, toString</li>
 *   <li>{@code @AllArgsConstructor} - all-fields constructor (works with @Builder)</li>
 *   <li>{@code @Builder}         - fluent builder used in service layer for entity → DTO mapping</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    /**
     * The unique database-assigned ID of the product.
     * Declared {@code final} to signal immutability — once set, the ID should never change.
     * Used by Thymeleaf to build URLs like: {@code /product/edit/3} and {@code /product/delete/3}.
     */
    @NotNull(message = "ID can't be null")
    private final Long id;

    /**
     * The name of the product.
     * <ul>
     *   <li>{@code @NotBlank}  — must be non-null and non-blank</li>
     *   <li>{@code @ValidName} — must contain only letters and spaces (no digits or symbols)</li>
     * </ul>
     */
    @NotBlank(message = "Product name cannot be empty")
    @ValidName(message = "Product name must contain only letters and spaces")
    private String productName;

    /**
     * The price of the product.
     * Must be non-null and zero-or-positive in a valid response.
     */
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    /**
     * The category of the product.
     * Must be non-null and non-blank in a valid response.
     */
    @NotBlank(message = "Category cannot be empty")
    private String category;
}
