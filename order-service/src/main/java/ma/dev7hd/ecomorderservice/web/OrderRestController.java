package ma.dev7hd.ecomorderservice.web;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.entities.ProductItem;
import ma.dev7hd.ecomorderservice.models.Product;
import ma.dev7hd.ecomorderservice.services.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderRestController {
    private final IOrderService orderService;

    /**
     * Retrieves a list of all orders.
     *
     * @return a {@code ResponseEntity} containing a {@code List} of {@code Order} objects.
     */
    @GetMapping("/all")
    ResponseEntity<List<Order>> getOrders(){
        return orderService.getOrders();
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the unique identifier of the order to retrieve
     * @return a {@code ResponseEntity} containing the requested {@code Order} if found,
     *         or appropriate error response if the order does not exist
     */
    @GetMapping("/{id}")
    ResponseEntity<Order> getOrder(@PathVariable Long id){
        return orderService.getOrder(id);
    }

    /**
     * Creates a new order based on the provided list of products.
     *
     * @param products a list of {@code Product} objects representing the products to be included in the order
     * @return a {@code ResponseEntity} containing the created {@code Order} object
     */
    @PostMapping("/new")
    ResponseEntity<Order> createNewOrder(@RequestBody List<Product> products){
        return orderService.createNewOrder(products);
    }

    /**
     * Deletes an order with the specified ID.
     *
     * @param id the unique identifier of the order to be deleted
     */
    @DeleteMapping("/{id}/delete")
    void deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
    }

    /**
     * Confirms an order by its ID. Updates the status of the specified order
     * in the system to signify it has been confirmed.
     *
     * @param orderId the unique identifier of the order to be confirmed
     * @return a ResponseEntity containing a message indicating the result of the operation
     */
    @PatchMapping("/{id}/confirm")
    ResponseEntity<String> confirmOrder(@PathVariable(name = "id") Long orderId){
        return orderService.confirmOrder(orderId);
    }

    /**
     * Cancels an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to be canceled
     * @return a ResponseEntity containing a confirmation message for the canceled order
     */
    @PatchMapping("/{id}/cancel")
    ResponseEntity<String> cancelOrder(@PathVariable(name = "id") Long orderId){
        return orderService.cancelOrder(orderId);
    }

    /**
     * Updates the status of an order to "delivered" based on the provided order ID.
     *
     * @param orderId the ID of the order to be marked as delivered
     * @return a ResponseEntity containing a message indicating the result of the operation
     */
    @PatchMapping("/{id}/deliver")
    ResponseEntity<String> deliverOrder(@PathVariable(name = "id") Long orderId){
        return orderService.deliverOrder(orderId);
    }

    /**
     * Processes the test endpoint by invoking the printProducts method of the order service and
     * returns a response entity with a static "test" string as the message.
     *
     * @return a {@code ResponseEntity} containing a "test" message as the response body.
     */
    @GetMapping("/test")
    ResponseEntity<String> test(){
        orderService.printProducts();
        return ResponseEntity.ok("test");
    }

    /**
     * Fetches and returns a list of all ProductItem entities from the underlying service.
     *
     * @return a list of ProductItem objects representing the product items associated with orders.
     */
    @GetMapping("/product-items")
    List<ProductItem> getProductItems(){
        return orderService.getProductItems();
    }
}
