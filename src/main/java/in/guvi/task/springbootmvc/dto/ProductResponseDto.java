package in.guvi.task.springbootmvc.dto;

import in.guvi.task.springbootmvc.validations.annotations.NoSpaces;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    @NotNull(message = "ID can't be null")
    private final Long id;

    @NotBlank(message = "Product name cannot be empty")
    private String productName; // Removed 'final'

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    @NotBlank(message = "Category cannot be empty")
    private String category;
}
