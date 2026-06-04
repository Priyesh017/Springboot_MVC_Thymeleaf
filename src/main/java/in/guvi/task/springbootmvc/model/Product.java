package in.guvi.task.springbootmvc.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity representing a "Product" record in the MySQL database.
 *
 * <p>This class is the "M" (Model) in the MVC pattern and maps directly to the
 * {@code Product} table in the database. Hibernate manages all SQL generation
 * for this entity (INSERT, SELECT, UPDATE, DELETE).
 *
 * <p>Lombok annotations used:
 * <ul>
 *   <li>{@code @Getter}          - generates getter methods for all fields</li>
 *   <li>{@code @Setter}          - generates setter methods for all fields</li>
 *   <li>{@code @NoArgsConstructor} - generates a no-argument constructor (required by JPA spec)</li>
 *   <li>{@code @AllArgsConstructor} - generates a constructor with all fields as parameters</li>
 *   <li>{@code @Builder}         - enables the fluent builder pattern (e.g., {@code Product.builder().productName(...).build()})</li>
 *   <li>{@code @ToString}        - generates a human-readable {@code toString()} for logging/debugging</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "Product")  // Maps this entity to the "Product" table in the database
public class Product {

    /**
     * Primary key — auto-incremented by the database.
     * Using {@code GenerationType.IDENTITY} means MySQL assigns the ID on each INSERT automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The name/title of the product. Maps to column "ProductName". */
    @Column(name = "ProductName")
    private String productName;

    /** The price of the product. Maps to column "ProductPrice". Stored as a floating-point value. */
    @Column(name = "ProductPrice")
    private Double price;

    /** The category/classification of the product (e.g., Electronics, Books). Maps to column "Category". */
    @Column(name = "Category")
    private String category;
}
