package in.guvi.task.springbootmvc.dto;

import in.guvi.task.springbootmvc.validations.annotations.NoSpaces;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequestDto {

    @NotNull(message = "Reader name can't be null")
    @NotEmpty(message = "Reader name can't be empty")
    @NotBlank(message = "Reader name cannot consist of only empty spaces")
    private String name;

    @NotNull(message = "Book name can't be null")
    @NotEmpty(message = "Book name can't be empty")
    @NotBlank(message = "Book name cannot consist of only empty spaces")
    private String bookName;

    @NotNull(message = "Feedback can't be null")
    @NotEmpty(message = "Feedback can't be empty")
    @NotBlank(message = "Feedback cannot consist of only empty spaces")
    private String feedback;
}
