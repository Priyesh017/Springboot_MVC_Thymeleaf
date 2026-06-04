package in.guvi.task.springbootmvc.validations.annotations;

import in.guvi.task.springbootmvc.validations.NoSpacesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom Jakarta Validation annotation that prohibits space characters in a string field.
 *
 * <p>This annotation can be placed on any {@code String} field or parameter to enforce
 * that the value does not contain whitespace/space characters. It integrates seamlessly
 * with the existing Spring MVC validation pipeline ({@code @Valid} / {@code @Validated}).
 *
 * <p>Example usage on a DTO field:
 * <pre>{@code
 *   @NoSpaces(message = "Username must not contain spaces")
 *   private String username;
 * }</pre>
 *
 * <p>Annotation meta-annotations explained:
 * <ul>
 *   <li>{@code @Target} — specifies where this annotation can be applied:
 *       on class fields ({@code FIELD}) or method/constructor parameters ({@code PARAMETER})</li>
 *   <li>{@code @Retention(RUNTIME)} — the annotation is retained at runtime, allowing
 *       the Jakarta Validation framework to read it via reflection</li>
 *   <li>{@code @Constraint(validatedBy)} — links this annotation to its validator implementation
 *       ({@link NoSpacesValidator}), which contains the actual validation logic</li>
 *   <li>{@code @Documented} — includes this annotation in generated Javadoc output</li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Can be applied to fields or parameters
@Retention(RetentionPolicy.RUNTIME)                 // Keep it alive at runtime for reflection
@Constraint(validatedBy = NoSpacesValidator.class)  // Link to our validation logic class
@Documented
public @interface NoSpaces {

    /**
     * The default validation failure message returned when a space is detected.
     * Can be overridden at the point of use:
     * {@code @NoSpaces(message = "Custom error message here")}
     */
    String message() default "Spaces are not allowed in this field";

    /**
     * Constraint groups — allows validation to be triggered selectively.
     * Required boilerplate by the Jakarta Validation specification.
     */
    Class<?>[] groups() default {};

    /**
     * Payload — allows associating metadata with the constraint (e.g., severity level).
     * Required boilerplate by the Jakarta Validation specification.
     */
    Class<? extends Payload>[] payload() default {};
}
