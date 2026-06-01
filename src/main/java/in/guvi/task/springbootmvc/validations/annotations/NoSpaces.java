package in.guvi.task.springbootmvc.validations.annotations;

import in.guvi.task.springbootmvc.validations.NoSpacesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER}) // Can be applied to fields or parameters
@Retention(RetentionPolicy.RUNTIME)                 // Keep it alive at runtime
@Constraint(validatedBy = NoSpacesValidator.class)  // Link to our validation logic class
@Documented
public @interface NoSpaces {

    // The default message if validation fails (Can be overridden by user)
    String message() default "Spaces are not allowed in this field";

    // Required by the Jakarta Validation spec boilerplate
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
