package in.guvi.task.springbootmvc.validations.annotations;

import in.guvi.task.springbootmvc.validations.ValidNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom Jakarta Validation annotation that restricts a string field to contain
 * only alphabetic characters (letters) and single spaces between words.
 *
 * <p>This validator is specifically designed for <em>name</em> fields — such as a
 * reader's name or a product name — where digits, symbols, and special characters
 * are semantically invalid inputs. For example:
 * <ul>
 *   <li>{@code "John Doe"}   → ✅ valid (letters and a single space)</li>
 *   <li>{@code "John123"}    → ❌ invalid (contains digits)</li>
 *   <li>{@code "John@Doe"}   → ❌ invalid (contains special character)</li>
 *   <li>{@code "  "}         → ❌ invalid (only spaces, no letters)</li>
 * </ul>
 *
 * <p>This annotation composes well with standard constraints:
 * <pre>{@code
 *   @NotBlank
 *   @ValidName
 *   private String name;
 * }</pre>
 *
 * <p>Annotation meta-annotations explained:
 * <ul>
 *   <li>{@code @Target(FIELD, PARAMETER)} — can be placed on class fields or method parameters</li>
 *   <li>{@code @Retention(RUNTIME)}       — retained at runtime so Jakarta Validation can read it via reflection</li>
 *   <li>{@code @Constraint(validatedBy)}  — wires this annotation to {@link ValidNameValidator},
 *                                           which implements the actual validation logic</li>
 *   <li>{@code @Documented}               — includes this annotation in generated Javadoc</li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidNameValidator.class)  // Delegate validation logic to ValidNameValidator
@Documented
public @interface ValidName {

    /**
     * The default error message shown to the user when validation fails.
     * Can be overridden at the usage site:
     * {@code @ValidName(message = "Reader name must contain only letters")}
     */
    String message() default "Name must contain only letters and spaces (no digits or special characters)";

    /**
     * Validation groups — allows selective triggering of this constraint.
     * Required boilerplate by the Jakarta Validation specification.
     */
    Class<?>[] groups() default {};

    /**
     * Payload — allows attaching metadata (e.g., severity) to the constraint.
     * Required boilerplate by the Jakarta Validation specification.
     */
    Class<? extends Payload>[] payload() default {};
}
