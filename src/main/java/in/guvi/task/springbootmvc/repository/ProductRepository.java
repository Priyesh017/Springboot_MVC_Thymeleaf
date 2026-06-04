package in.guvi.task.springbootmvc.repository;

import in.guvi.task.springbootmvc.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>By extending {@link JpaRepository}{@code <Product, Long>}, Spring Data JPA automatically
 * generates a full implementation of this interface at runtime — no boilerplate DAO code needed.
 *
 * <p>Operations inherited from {@link JpaRepository} (among others):
 * <ul>
 *   <li>{@code save(entity)}        — INSERT or UPDATE depending on whether the entity has an ID</li>
 *   <li>{@code findById(id)}        — SELECT by primary key → returns {@code Optional<Product>}</li>
 *   <li>{@code findAll()}           — SELECT * FROM Product → returns {@code List<Product>}</li>
 *   <li>{@code deleteById(id)}      — DELETE FROM Product WHERE id = ?</li>
 *   <li>{@code existsById(id)}      — check if a record exists without loading it</li>
 *   <li>{@code count()}             — total number of records in the Product table</li>
 * </ul>
 *
 * <p>Custom finder methods or JPQL queries can be added here when needed.
 * Example: {@code List<Product> findByCategory(String category);} would automatically
 * generate a query filtering products by category.
 *
 * <p>{@code @Repository} marks this as a Spring-managed bean and enables
 * Spring's exception translation mechanism for database errors.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // No custom methods needed for now — JpaRepository covers all required operations
}
