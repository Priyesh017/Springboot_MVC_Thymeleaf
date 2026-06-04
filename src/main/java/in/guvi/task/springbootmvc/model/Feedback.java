package in.guvi.task.springbootmvc.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity representing a "Feedback" record in the MySQL database.
 *
 * <p>This class is the "M" (Model) in the MVC pattern and maps directly to the
 * {@code Feedback} table in the database. Hibernate manages all SQL generation
 * for this entity (INSERT, SELECT, UPDATE, DELETE).
 *
 * <p>Lombok annotations used:
 * <ul>
 *   <li>{@code @Getter}          - generates getter methods for all fields</li>
 *   <li>{@code @Setter}          - generates setter methods for all fields</li>
 *   <li>{@code @NoArgsConstructor} - generates a no-argument constructor (required by JPA spec)</li>
 *   <li>{@code @AllArgsConstructor} - generates a constructor with all fields as parameters</li>
 *   <li>{@code @Builder}         - enables the fluent builder pattern (e.g., {@code Feedback.builder().name(...).build()})</li>
 *   <li>{@code @ToString}        - generates a human-readable {@code toString()} method for debugging</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "Feedback")  // Maps this entity to the "Feedback" table in the database
public class Feedback {

    /**
     * Primary key — auto-incremented by the database using an IDENTITY strategy.
     * {@code @GeneratedValue(strategy = GenerationType.IDENTITY)} lets MySQL assign the ID
     * automatically on each INSERT, so we never set it manually.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The name of the reader/user submitting the feedback. Maps to column "ReaderName". */
    @Column(name = "ReaderName")
    private String name;

    /** The title of the book the feedback is about. Maps to column "BookName". */
    @Column(name = "BookName")
    private String bookName;

    /** The actual feedback text/review content. Maps to column "Feedback". */
    @Column(name = "Feedback")
    private String feedback;
}
