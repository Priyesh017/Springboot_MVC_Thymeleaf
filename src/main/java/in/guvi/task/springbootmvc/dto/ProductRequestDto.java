package in.guvi.task.springbootmvc.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for incoming product creation and update form submissions.
 *
 * <p>This DTO acts as the contract between the HTML form layer (Thymeleaf) and the
 * service layer. It does <em>not</em> include the {@code id} field since the ID is
 * supplied via the URL path variable, not the form body.
 *
 * <p>Bean Validation annotations are applied to enforce input integrity before
 * the data ever reaches the service or database layer:
 * <ul>
 *   <li>{@code @NotBlank} — rejects null, empty, and whitespace-only strings</li>
 *   <li>{@code @NotNull}  — rejects null numeric values (can't use @NotBlank on Double)</li>
 *   <li>{@code @Min(0)}   — ensures price is zero or positive (no negatives)</li>
 * </ul>
 *
 * <p>Lombok annotations:
 * <ul>
 *   <li>{@code @Data}            - generates getters, setters, equals, hashCode, toString</li>
 *   <li>{@code @AllArgsConstructor} - all-fields constructor</li>
 *   <li>{@code @NoArgsConstructor}  - no-args constructor (required by Spring MVC for form binding)</li>
 *   <li>{@code @Builder}         - fluent builder used in service and test code</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDto {

    /**
     * The product name/title submitted via the form.
     * Cannot be null, empty, or whitespace-only.
     */
    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    /**
     * The price of the product.
     * {@code @NotNull} is used (not @NotBlank) because this is a {@code Double}, not a String.
     * {@code @Min(0)} ensures the price is not negative.
     */
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    /**
     * The product category (e.g., "Electronics", "Books", "Clothing").
     * Cannot be null, empty, or whitespace-only.
     */
    @NotBlank(message = "Category cannot be empty")
    private String category;
}