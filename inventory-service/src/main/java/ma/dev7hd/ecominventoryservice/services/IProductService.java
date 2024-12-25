package ma.dev7hd.ecominventoryservice.services;

import ma.dev7hd.ecominventoryservice.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    ResponseEntity<Product> getProductById(UUID id);

    Page<Product> getProductsByCriteria(UUID id, String name, String description, Double minPrice, Double maxPrice, Integer minQuantity, Integer maxQuantity, Pageable pageable);

    ResponseEntity<List<Product>> getAllProducts();

    @Transactional
    ResponseEntity<Product> addProduct(Product product);

    @Transactional
    ResponseEntity<Product> updateProduct(UUID id, Product product);

    void deleteProduct(UUID id);

    @Transactional
    Integer incrementProductQuantity(UUID id, Integer quantity);

    @Transactional
    Integer decrementProductQuantity(UUID id, Integer quantity);
}
