package in.guvi.task.springbootmvc.service;

import in.guvi.task.springbootmvc.dto.ProductRequestDto;
import in.guvi.task.springbootmvc.dto.ProductResponseDto;
import in.guvi.task.springbootmvc.model.Product;
import in.guvi.task.springbootmvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class encapsulating all business logic for the Product module.
 *
 * <p>This is the "Business Logic" layer in the layered MVC architecture:
 * <pre>
 *   Controller  →  Service  →  Repository  →  Database
 * </pre>
 *
 * <p>Responsibilities of this service:
 * <ul>
 *   <li>Map incoming {@link ProductRequestDto} objects to {@link Product} JPA entities</li>
 *   <li>Map {@link Product} entities back to {@link ProductResponseDto} objects for the view</li>
 *   <li>Coordinate CRUD operations via {@link ProductRepository}</li>
 *   <li>Throw meaningful runtime exceptions when a product is not found</li>
 * </ul>
 *
 * <p>{@code @Service} registers this class as a Spring-managed service bean.
 * {@code @RequiredArgsConstructor} (Lombok) injects {@link ProductRepository} via constructor.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    /** Repository providing CRUD operations for {@link Product} entities via Spring Data JPA. */
    private final ProductRepository productRepository;

    /**
     * Retrieves all products from the database and maps them to response DTOs.
     *
     * <p>Uses Java Streams to perform a declarative transformation:
     * {@code List<Product>} → {@code List<ProductResponseDto>}.
     * Each entity is converted using a builder, ensuring the view layer receives
     * only the necessary data, including the product ID for action links.
     *
     * @return a list of {@link ProductResponseDto} representing all stored products
     */
    public List<ProductResponseDto> displayProducts() {
        // SQL: SELECT * FROM Product — retrieve all records as JPA entities
        List<Product> products = productRepository.findAll();

        // Stream + map: convert each Product entity → ProductResponseDto
        return products.stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .category(product.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Persists a new product to the database.
     *
     * <p>Maps the incoming {@link ProductRequestDto} to a {@link Product} entity
     * (ID is excluded — the database auto-generates it via IDENTITY strategy).
     * Delegates persistence to the repository, which issues a SQL INSERT.
     *
     * @param dto the product data submitted via the add-product form
     * @return the saved {@link Product} entity, including the auto-generated ID
     */
    public Product saveProduct(ProductRequestDto dto) {
        // Map DTO → Entity using Lombok's builder (no ID set — auto-assigned by DB)
        Product product = Product.builder()
                .productName(dto.getProductName())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build();

        // SQL: INSERT INTO Product (ProductName, ProductPrice, Category) VALUES (?, ?, ?)
        return productRepository.save(product);
    }

    /**
     * Fetches a single product by ID and maps it to a {@link ProductRequestDto}
     * suitable for pre-populating the product edit form.
     *
     * <p>The {@link ProductRequestDto} is used (instead of Response) because the
     * edit form only binds to editable fields — the ID is handled via the URL path.
     *
     * @param id the unique database ID of the product to retrieve
     * @return a {@link ProductRequestDto} populated with the current product field values
     * @throws RuntimeException if no product exists with the given ID
     */
    public ProductRequestDto getProductByIdForUpdate(Long id) {
        // findById returns Optional<Product>; orElseThrow unwraps or throws if not found
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Map entity → RequestDto (editable fields only, no ID)
        return ProductRequestDto.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

    /**
     * Updates an existing product's data with new values submitted from the edit form.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Fetch the managed {@link Product} entity by its ID (throws if absent).</li>
     *   <li>Apply the new values from the DTO to the entity using Lombok setters.</li>
     *   <li>Re-save the modified entity; Hibernate detects the changes and issues a SQL UPDATE.</li>
     * </ol>
     *
     * @param id  the unique database ID of the product to update
     * @param dto the updated product data from the edit form
     * @throws RuntimeException if no product exists with the given ID
     */
    public void updateProduct(Long id, ProductRequestDto dto) {
        // Retrieve the existing record; throws if the product ID is invalid
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Overwrite the entity's fields with the new values from the submitted form
        existingProduct.setProductName(dto.getProductName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setCategory(dto.getCategory());

        // SQL: UPDATE Product SET ProductName=?, ProductPrice=?, Category=? WHERE id=?
        productRepository.save(existingProduct);
    }

    /**
     * Deletes a product record from the database by its ID.
     *
     * <p>Delegates to {@code productRepository.deleteById()}, which issues a SQL DELETE statement.
     * If no product exists with the given ID, Spring Data JPA silently ignores the operation.
     *
     * @param id the unique database ID of the product to delete
     */
    public void deleteProduct(Long id) {
        // SQL: DELETE FROM Product WHERE id = ?
        productRepository.deleteById(id);
    }
}
