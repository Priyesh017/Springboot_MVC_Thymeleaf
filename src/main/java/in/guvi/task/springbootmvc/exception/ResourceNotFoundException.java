package in.guvi.task.springbootmvc.exception;

/**
 * Custom runtime exception used to indicate that a requested resource could not be found.
 *
 * This is typically thrown from the Service layer when a database query yields no results
 * (e.g., searching for an Employee ID that does not exist).
 * The GlobalExceptionHandler intercepts this exception and translates it into an HTTP 404 (Not Found) response.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message A descriptive message explaining exactly what was not found
     *                (e.g., "Employee not found with id: EMP123"). This message
     *                is ultimately passed to the client in the ErrorResponse payload.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}