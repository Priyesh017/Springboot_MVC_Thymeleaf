package in.guvi.task.springbootmvc.validations;

import in.guvi.task.springbootmvc.validations.annotations.ValidName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom constraint validator that implements the logic for the {@link ValidName} annotation.
 *
 * <p>Implements {@link ConstraintValidator}{@code <ValidName, String>}, meaning it validates
 * {@code String} fields annotated with {@code @ValidName}. Jakarta Validation automatically
 * calls {@link #isValid(String, ConstraintValidatorContext)} during form submission or
 * whenever {@code @Valid} is triggered on a controller method parameter.
 *
 * <h3>Validation Rule</h3>
 * <p>The value must match the regex pattern: {@code ^[a-zA-Z]+(\\s[a-zA-Z]+)*$}
 * <ul>
 *   <li>{@code ^[a-zA-Z]+}         — must start with one or more letters</li>
 *   <li>{@code (\\s[a-zA-Z]+)*}    — optionally followed by groups of: exactly one space + one or more letters</li>
 *   <li>{@code $}                  — must end there (no trailing spaces, no digits, no symbols)</li>
 * </ul>
 *
 * <h3>Examples</h3>
 * <pre>
 *   "John"          → ✅ valid
 *   "John Doe"      → ✅ valid (single space between words)
 *   "Mary Jane Lee" → ✅ valid
 *   "John123"       → ❌ invalid (contains digit)
 *   "John@Doe"      → ❌ invalid (contains special character)
 *   "John  Doe"     → ❌ invalid (double space)
 *   "  John"        → ❌ invalid (leading space)
 *   " "             → ❌ invalid (only space)
 *   null            → ✅ passes (let @NotNull / @NotBlank handle null separately)
 * </pre>
 *
 * <h3>Why this matters</h3>
 * <p>Without this validator, a user could enter values like {@code "John123"}, {@code "@#$%"},
 * or even script-like characters in name fields. This validator prevents such invalid inputs
 * from reaching the database and ensures data integrity for name-type fields.
 */
public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    /**
     * Regex pattern allowing only alphabetic words separated by single spaces.
     * Compiled once per validator instance for performance.
     */
    private static final String NAME_REGEX = "^[a-zA-Z]+(\\s[a-zA-Z]+)*$";

    /**
     * Validates that the given string is a properly formatted name.
     *
     * <p>Null values are treated as valid — this validator is intentionally null-tolerant,
     * following the Jakarta Validation convention: null-checking is delegated to {@code @NotNull}
     * or {@code @NotBlank} so that constraints compose cleanly without duplicate null logic.
     *
     * @param value   the string value of the annotated field (e.g., a reader's name)
     * @param context provides context for building custom violation messages if needed
     * @return {@code true} if the value is null (deferred to @NotNull) or matches the name regex;
     *         {@code false} if the value contains digits, special characters, leading/trailing spaces,
     *         or consecutive spaces
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are acceptable here — @NotNull / @NotBlank handles null separately
        if (value == null) {
            return true;
        }

        // Check the value against the alphabetic-only + single-space regex pattern
        return value.matches(NAME_REGEX);
    }
}
