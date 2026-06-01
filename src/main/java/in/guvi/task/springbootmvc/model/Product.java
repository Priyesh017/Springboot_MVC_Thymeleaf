package in.guvi.task.springbootmvc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "ProductName")
    private String productName;
    @Column(name = "ProductPrice")
    private Double price;
    @Column(name = "Category")
    private String category;
}
