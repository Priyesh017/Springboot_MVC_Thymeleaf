package in.guvi.task.springbootmvc.service;

import in.guvi.task.springbootmvc.dto.FeedbackRequestDto;
import in.guvi.task.springbootmvc.dto.FeedbackResponseDto;
import in.guvi.task.springbootmvc.model.Feedback;
import in.guvi.task.springbootmvc.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class encapsulating all business logic for the Feedback module.
 *
 * <p>This is the "Business Logic" layer in the layered MVC architecture:
 * <pre>
 *   Controller  →  Service  →  Repository  →  Database
 * </pre>
 *
 * <p>Responsibilities of this service:
 * <ul>
 *   <li>Map incoming DTOs to JPA entities before persisting</li>
 *   <li>Map JPA entities back to DTOs before returning to the controller</li>
 *   <li>Coordinate CRUD operations via the repository</li>
 *   <li>Throw meaningful exceptions when a resource is not found</li>
 * </ul>
 *
 * <p>{@code @Service} marks this as a Spring-managed service bean (picked up by component scanning).
 * {@code @RequiredArgsConstructor} (Lombok) generates a constructor for the {@code final}
 * {@link FeedbackRepository} field, enabling constructor-based dependency injection.
 */
@Service
@RequiredArgsConstructor
public class FeedbackService {

    /** Repository interface for database CRUD operations on {@link Feedback} entities. */
    private final FeedbackRepository feedbackRepository;

    /**
     * Saves a new feedback entry to the database.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Map the incoming {@link FeedbackRequestDto} to a {@link Feedback} JPA entity
     *       using the builder pattern (ID is not set — the database auto-generates it).</li>
     *   <li>Persist the entity via {@code feedbackRepository.save()}, which executes an SQL INSERT.</li>
     *   <li>Return the saved entity (now includes the auto-generated ID from the database).</li>
     * </ol>
     *
     * @param dto the form data received from the feedback submission form
     * @return the persisted {@link Feedback} entity with its generated ID
     */
    public Feedback saveFeedback(FeedbackRequestDto dto) {
        // Map DTO → Entity using the Lombok builder pattern
        Feedback feedback = Feedback.builder()
                .name(dto.getName())
                .bookName(dto.getBookName())
                .feedback(dto.getFeedback())
                .build();

        // Persist to database; JPA issues an INSERT statement and returns the saved entity
        return feedbackRepository.save(feedback);
    }

    /**
     * Retrieves all feedback records from the database and maps them to response DTOs.
     *
     * <p>Uses Java Streams to transform the list of {@link Feedback} entities
     * into a list of {@link FeedbackResponseDto} objects using a builder-based mapping.
     * The Response DTO includes the {@code id} field so the view can generate
     * edit and delete action links.
     *
     * @return a list of {@link FeedbackResponseDto} objects representing all stored feedback
     */
    public List<FeedbackResponseDto> displayAllFeedbacks() {
        return feedbackRepository.findAll()  // SQL: SELECT * FROM Feedback
                .stream()
                // Map each Feedback entity → FeedbackResponseDto using builder
                .map(f -> FeedbackResponseDto.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .bookName(f.getBookName())
                        .feedback(f.getFeedback())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Fetches a single feedback record by ID and maps it to a {@link FeedbackRequestDto}
     * for pre-populating the update/edit form.
     *
     * <p>Returns a {@link FeedbackRequestDto} (not Response) because the edit form
     * only needs the editable field values (name, bookName, feedback) — not the ID,
     * which is already in the URL path.
     *
     * @param id the database ID of the feedback to retrieve
     * @return a {@link FeedbackRequestDto} populated with the current field values
     * @throws RuntimeException if no feedback exists with the given ID
     */
    public FeedbackRequestDto getFeedbackByIdForUpdate(Long id) {
        // findById returns Optional<Feedback>; orElseThrow unwraps it or throws if absent
        Feedback f = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // Map entity → RequestDto (only editable fields, no ID)
        return FeedbackRequestDto.builder()
                .name(f.getName())
                .bookName(f.getBookName())
                .feedback(f.getFeedback())
                .build();
    }

    /**
     * Updates an existing feedback record with new values from the submitted edit form.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Fetch the existing {@link Feedback} entity by ID (throws if not found).</li>
     *   <li>Apply the new field values from the DTO directly to the entity using setters.</li>
     *   <li>Save the modified entity — JPA/Hibernate detects the change and issues an SQL UPDATE.</li>
     * </ol>
     *
     * @param id  the database ID of the feedback to update
     * @param dto the updated form data
     * @throws RuntimeException if no feedback exists with the given ID
     */
    public void updateFeedback(Long id, FeedbackRequestDto dto) {
        // Fetch the managed entity; throws if no record exists with this ID
        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        // Apply updated values from DTO onto the existing managed entity
        existing.setName(dto.getName());
        existing.setBookName(dto.getBookName());
        existing.setFeedback(dto.getFeedback());

        // Persist the updated entity — Hibernate issues a SQL UPDATE statement
        feedbackRepository.save(existing);
    }

    /**
     * Deletes a feedback record from the database by its ID.
     *
     * <p>Delegates directly to the repository's {@code deleteById} method,
     * which issues a SQL DELETE statement. If no record exists with the given ID,
     * Spring Data JPA silently ignores the operation (no exception is thrown).
     *
     * @param id the database ID of the feedback record to delete
     */
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);  // SQL: DELETE FROM Feedback WHERE id = ?
    }
}
