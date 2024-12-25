package ma.dev7hd.ecominventoryservice.services;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecominventoryservice.entities.Product;
import ma.dev7hd.ecominventoryservice.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductServiceImp implements IProductService {
    private final ProductRepository productRepository;

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product to be retrieved
     * @return a {@link ResponseEntity} containing the product if found, or a not found response if the product does not exist
     */
    @Override
    public ResponseEntity<Product> getProductById(UUID id) {
        Product product = productRepository.findById(id).orElse(null);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves a paginated list of products based on the specified filtering criteria.
     *
     * @param id the unique identifier of the product, or null to ignore this criterion
     * @param name the name of the product, or null to ignore this criterion
     * @param description the description of the product, or null to ignore this criterion
     * @param minPrice the minimum price of the product, or null to ignore this criterion
     * @param maxPrice the maximum price of the product, or null to ignore this criterion
     * @param minQuantity the minimum available quantity of the product, or null to ignore this criterion
     * @param maxQuantity the maximum available quantity of the product, or null to ignore this criterion
     * @param pageable the pagination and sorting information
     * @return a page containing the products that match the given criteria
     */
    @Override
    public Page<Product> getProductsByCriteria(UUID id, String name, String description, Double minPrice, Double maxPrice, Integer minQuantity, Integer maxQuantity, Pageable pageable) {
        String productId = id != null ? id.toString().toLowerCase().trim() : null;
        String productName = name != null ? name.toLowerCase().trim() : null;
        String productDescription = description != null ? description.toLowerCase().trim() : null;

        return productRepository.getProductsByCriteria(
                productId,
                productName,
                productDescription,
                minPrice,
                maxPrice,
                minQuantity,
                maxQuantity,
                pageable
        );
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return a {@link ResponseEntity} containing the list of all products if the repository is not empty,
     *         or a bad request response if no products are found.
     */
    @Override
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.badRequest().build();
    }

    /**
     * Adds a new product to the repository and returns the saved product.
     *
     * @param product the product to be added
     * @return a {@link ResponseEntity} containing the saved product
     */
    @Transactional
    @Override
    public ResponseEntity<Product> addProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }
    /**
     * Updates an existing product in the repository with the provided details.
     * If the product with the specified ID exists, its fields are updated with non-null
     * values from the given product object. Otherwise, a not found response is returned.
     *
     * @param id the unique identifier of the product to be updated
     * @param product the product object containing the updated details
     * @return a {@link ResponseEntity} containing the updated product if the update is successful,
     *         or a not found response if the product with the specified ID does not exist
     */
    @Transactional
    @Override
    public ResponseEntity<Product> updateProduct(UUID id, Product product) {
        Product savedProduct = productRepository.findById(id).orElse(null);
        if (savedProduct != null) {
            if(product.getName() != null && !product.getName().isBlank()) savedProduct.setName(product.getName());
            if(product.getDescription() != null && !product.getDescription().isBlank()) savedProduct.setDescription(product.getDescription());
            if(product.getPrice() != null && product.getPrice() > 0) savedProduct.setPrice(product.getPrice());
            if(product.getQuantity() != null && product.getQuantity() >= 0) savedProduct.setQuantity(product.getQuantity());
            productRepository.save(savedProduct);
            return ResponseEntity.ok(savedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a product identified by the provided ID.
     *
     * @param id the unique identifier of the product to be deleted
     */
    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    /**
     * Increments the quantity of a product by a specified amount.
     * If the increment amount is less than or equal to zero, this method returns an empty Optional.
     * If the product is not found, an empty Optional is returned.
     * If successful, the product's quantity is updated and the new quantity is returned.
     *
     * @param productId the unique identifier of the product whose quantity is to be incremented
     * @param incrementAmount the amount to increment the product's quantity by; must be greater than zero
     * @return an Optional containing the updated quantity of the product if successful; otherwise, an empty Optional
     */
    @Transactional
    @Override
    public Integer incrementProductQuantity(UUID productId, Integer incrementAmount) {
        if (incrementAmount <= 0) {
            System.out.println("Increment amount must be greater than zero.");
            return null;
        }

        Product product = findProductById(productId);
        if (product == null) {
            System.out.printf("Product with id %s was not found.\n", productId);
            return null;
        }

        product.setQuantity(product.getQuantity() + incrementAmount);
        productRepository.save(product);
        return product.getQuantity();
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to find
     * @return the product corresponding to the given ID, or null if no product is found
     */
    private Product findProductById(UUID productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * Decreases the quantity of a product by the specified amount.
     *
     * @param id the unique identifier of the product whose quantity is to be decremented
     * @param quantity the amount by which the product's quantity should be reduced
     * @return the updated quantity of the product after the decrement
     * @throws IllegalArgumentException if the product*/
    @Transactional
    @Override
    public Integer decrementProductQuantity(UUID id, Integer quantity) {
        if (id == null || quantity == null) {
            throw new IllegalArgumentException("Product ID and quantity must not be null.");
        }

        Product product = productRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Product with ID " + id + " not found.")
        );

        validateDecrementQuantity(product, quantity);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        return product.getQuantity();
    }

    private void validateDecrementQuantity(Product product, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Decrement quantity must be greater than zero.");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock to decrement by " + quantity + ".");
        }
    }

}
