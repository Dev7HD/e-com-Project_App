package ma.dev7hd.ecomorderservice.feignClients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ma.dev7hd.ecomorderservice.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackGetProductById")
    ResponseEntity<Product> getProductById(@PathVariable UUID id);

    @GetMapping("/api/products/all")
    @CircuitBreaker(name = "inventory-service-0", fallbackMethod = "fallbackGetAllProducts")
    ResponseEntity<List<Product>> getAllProducts();

    @PostMapping("/api/products/{id}/decrement-quantity/{quantity}")
    @CircuitBreaker(name = "inventory-service-1", fallbackMethod = "fallbackDecrementProductQuantity")
    Integer decrementProductQuantity(@PathVariable UUID id,@PathVariable Integer quantity);

    @PostMapping("/api/products/{id}/increment-quantity/{quantity}")
    @CircuitBreaker(name = "inventory-service-2", fallbackMethod = "fallbackIncrementProductQuantity")
    Integer incrementProductQuantity(@PathVariable UUID id,@PathVariable Integer quantity);

    default ResponseEntity<?> fallbackIncrementProductQuantity(UUID id, int quantity, Exception e) {
        System.out.println("IncrementProductQuantity: Inventory service not available.");
        return ResponseEntity.notFound().build();
    }

    default Integer fallbackDecrementProductQuantity(UUID id, Integer quantity, Exception e) {
        System.out.println("DecrementProductQuantity: Inventory service not available.");
        System.err.println("Error: " + e.getMessage());
        return null;
    }

    default ResponseEntity<?> fallbackGetAllProducts(Exception e) {
        System.out.println("GetAllProducts: Inventory service not available.");
        return ResponseEntity.notFound().build();
    }

    default ResponseEntity<Product> fallbackGetProductById(UUID id, Exception e) {
        System.out.println("GetProductById: Inventory service not available.");
        return ResponseEntity.notFound().build();
    }
}
