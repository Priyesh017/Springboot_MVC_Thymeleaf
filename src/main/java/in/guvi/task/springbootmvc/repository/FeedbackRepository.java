package in.guvi.task.springbootmvc.repository;

import in.guvi.task.springbootmvc.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Feedback} entities.
 *
 * <p>By extending {@link JpaRepository}{@code <Feedback, Long>}, Spring Data JPA automatically
 * provides a complete set of CRUD operations at runtime — no implementation class is needed.
 *
 * <p>Operations inherited from {@link JpaRepository} (among others):
 * <ul>
 *   <li>{@code save(entity)}        — INSERT or UPDATE (based on whether the ID is set)</li>
 *   <li>{@code findById(id)}        — SELECT by primary key → returns {@code Optional<Feedback>}</li>
 *   <li>{@code findAll()}           — SELECT all records → returns {@code List<Feedback>}</li>
 *   <li>{@code deleteById(id)}      — DELETE by primary key</li>
 *   <li>{@code existsById(id)}      — check existence without loading the entity</li>
 *   <li>{@code count()}             — returns the total number of records in the table</li>
 * </ul>
 *
 * <p>Custom query methods can be added here using Spring Data JPA's
 * method name convention (e.g., {@code findByBookName(String bookName)}) or
 * via {@code @Query} annotations for custom JPQL/SQL.
 *
 * <p>{@code @Repository} marks this interface as a Spring-managed repository bean
 * and enables translation of low-level database exceptions into Spring's
 * {@link org.springframework.dao.DataAccessException} hierarchy.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // No custom methods needed for now — JpaRepository covers all required operations
}
