package in.guvi.task.springbootmvc.validations;

import in.guvi.task.springbootmvc.validations.annotations.NoSpaces;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements ConstraintValidator<NoSpaces, String> {

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
