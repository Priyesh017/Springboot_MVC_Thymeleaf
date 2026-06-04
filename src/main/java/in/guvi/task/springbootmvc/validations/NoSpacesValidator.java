package in.guvi.task.springbootmvc.validations;

import in.guvi.task.springbootmvc.validations.annotations.NoSpaces;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom constraint validator that enforces the {@link NoSpaces} annotation.
 *
 * <p>Implements {@link ConstraintValidator}{@code <NoSpaces, String>} which requires
 * implementing the {@link #isValid(String, ConstraintValidatorContext)} method.
 * When the {@link NoSpaces} annotation is placed on a field, Jakarta Validation
 * delegates the validation logic to this class at runtime.
 *
 * <p>How it works:
 * <ol>
 *   <li>Jakarta Validation detects the {@code @NoSpaces} annotation on a field.</li>
 *   <li>It instantiates this validator and calls {@code isValid()} with the field's value.</li>
 *   <li>If {@code isValid()} returns {@code false}, a constraint violation is raised
 *       and the configured {@code message} is added to the {@link org.springframework.validation.BindingResult}.</li>
 * </ol>
 */
public class NoSpacesValidator implements ConstraintValidator<NoSpaces, String> {

    /**
     * Validates that the given string value contains no space characters.
     *
     * <p>Null values are treated as valid — the responsibility of rejecting null
     * belongs to {@code @NotNull}, not to this validator. This follows the
     * Jakarta Validation convention of composing multiple constraints.
     *
     * @param value   the string value of the annotated field to validate
     * @param context provides context information for building custom constraint violations
     * @return {@code true} if the value is null (defer to @NotNull) or contains no spaces;
     *         {@code false} if the value contains at least one space character
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 1. If the field is null, we pass validation. Let @NotNull handle null checks.
        if (value == null) {
            return true;
        }

        // 2. Return true if there are no spaces, false if a space is found
        return !value.contains(" ");
    }
}
