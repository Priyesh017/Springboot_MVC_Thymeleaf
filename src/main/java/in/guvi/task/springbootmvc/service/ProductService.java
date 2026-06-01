package in.guvi.task.springbootmvc.service;

import in.guvi.task.springbootmvc.dto.ProductRequestDto;
import in.guvi.task.springbootmvc.dto.ProductResponseDto;
import in.guvi.task.springbootmvc.model.Product;
import in.guvi.task.springbootmvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto> displayProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .category(product.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    public Product saveProduct(ProductRequestDto dto) {
        Product product = Product.builder()
                .productName(dto.getProductName())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build();

        return productRepository.save(product);
    }

    public ProductRequestDto getProductByIdForUpdate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductRequestDto.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .category(product.getCategory())
                .build();
    }

    public void updateProduct(Long id, ProductRequestDto dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setProductName(dto.getProductName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setCategory(dto.getCategory());

        productRepository.save(existingProduct);
    }

    // Delete a product
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
