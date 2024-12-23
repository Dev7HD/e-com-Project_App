package ma.dev7hd.ecominventoryservice.web;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecominventoryservice.entities.Product;
import ma.dev7hd.ecominventoryservice.services.IProductService;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<Product> updateProduct(@PathVariable UUID id,@RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    /**
     * Deletes a product identified by its unique ID.
     *
     * @param id the unique identifier of the product to be deleted
     */
    @DeleteMapping("/{id}/delete")
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
    @PostMapping(value = "/{id}/increment-quantity", produces = "application/json")
    Integer incrementProductQuantity(@PathVariable UUID id, @RequestParam Integer quantity) {
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
}
