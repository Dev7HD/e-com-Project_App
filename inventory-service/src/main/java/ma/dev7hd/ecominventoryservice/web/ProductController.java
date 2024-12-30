package ma.dev7hd.ecominventoryservice.web;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecominventoryservice.entities.Product;
import ma.dev7hd.ecominventoryservice.services.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final IProductService productService;

    /**
     * Retrieves a list of all products.
     *
     * @return ResponseEntity containing a list of Product objects.
     */
    @GetMapping(value = "/all", produces = "application/json")
    ResponseEntity<List<Product>> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product to retrieve
     * @return a {@link ResponseEntity} containing the product if found, or an appropriate error response if not found
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    /**
     * Creates a new product entry in the system.
     *
     * @param product the product details to create the new product
     * @return a response entity containing the created product
     */
    @PostMapping(value = "/new", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    /**
     * Updates an existing product with the specified details.
     *
     * @param id the unique identifier of the product to be updated
     * @param product the product object containing the updated details
     * @return a ResponseEntity containing the updated product
     */
    @PatchMapping(value = "/{id}/update", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<Product> updateProduct(@PathVariable UUID id,@RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    /**
     * Deletes a product identified by its unique ID.
     *
     * @param id the unique identifier of the product to be deleted
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    /**
     * Increments the quantity of a product with the specified ID by the given amount.
     *
     * @param id the unique identifier of the product
     * @param quantity the amount by which the product quantity should be incremented
     * @return the updated quantity of the product after increment
     */
    @PostMapping(value = "/{id}/increment-quantity/{quantity}", produces = "application/json")
    Integer incrementProductQuantity(@PathVariable UUID id, @PathVariable Integer quantity) {
        return productService.incrementProductQuantity(id, quantity);
    }

    /**
     * Decrements the quantity of a product identified by its unique ID.
     *
     * @param id the unique identifier of the product whose quantity is to be decremented
     * @param quantity the amount by which the product quantity should be decreased
     * @return the updated quantity of the product after decrementing
     */
    @PostMapping(value = "/{id}/decrement-quantity/{quantity}", produces = "application/json")
    Integer decrementProductQuantity(@PathVariable UUID id, @PathVariable Integer quantity) {
        return productService.decrementProductQuantity(id, quantity);
    }

    /**
     * Retrieves a paginated list of products matching the given search criteria.
     *
     * @param id the unique identifier of the product to search for; can be null
     * @param name the name of the product to filter by; can be null
     * @param description the description of the product to filter by; can be null
     * @param minPrice the minimum price of the products to filter by; can be null
     * @param maxPrice the maximum price of the products to filter by; can be null
     * @param minQuantity the minimum quantity of the products to filter by; can be null
     * @param maxQuantity the maximum quantity of the products to filter by; can be null
     * @param page the page number for pagination (default is 0)
     * @param size the size of each page for pagination (default is 10)
     * @return a page of products that match the search criteria
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    Page<Product> searchProducts(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getProductsByCriteria(id, name, description, minPrice, maxPrice, minQuantity, maxQuantity, Pageable.ofSize(size).withPage(page));
    }
}
