package ma.dev7hd.ecomorderservice.web;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.entities.ProductItem;
import ma.dev7hd.ecomorderservice.enums.OrderState;
import ma.dev7hd.ecomorderservice.models.Product;
import ma.dev7hd.ecomorderservice.services.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderRestController {
    private final IOrderService orderService;

    /**
     * Retrieves a paginated list of orders based on the provided filters and pagination details.
     *
     * @param id an optional filter specifying the unique identifier of the order
     * @param orderState an optional filter specifying the state of the order
     * @param minPrice an optional filter specifying the minimum total price of the order
     * @param maxPrice an optional filter specifying the maximum total price of the order
     * @param minQuantity an optional filter specifying the minimum total quantity of items in the order
     * @param maxQuantity an optional filter specifying the maximum total quantity of items in the order
     * @param minItemQuantity an optional filter specifying the minimum quantity of a single item in the order
     * @param maxItemQuantity an optional filter specifying the maximum quantity of a single item in the order
     * @param page the requested page number for pagination, defaulting to 0
     * @param size the size of each page for pagination, defaulting to 10
     * @return a {@code ResponseEntity} containing a paginated list of {@code Order} objects that match the filters
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<Page<Order>> getOrders(
            @RequestParam(required = false, defaultValue = "") String id,
            @RequestParam(required = false, defaultValue = "") OrderState orderState,
            @RequestParam(required = false, defaultValue = "") Double minPrice,
            @RequestParam(required = false, defaultValue = "") Double maxPrice,
            @RequestParam(required = false, defaultValue = "") Integer minQuantity,
            @RequestParam(required = false, defaultValue = "") Integer maxQuantity,
            @RequestParam(required = false, defaultValue = "") Integer minItemQuantity,
            @RequestParam(required = false, defaultValue = "") Integer maxItemQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return orderService.getOrders(
                id,
                orderState,
                minPrice,
                maxPrice,
                minQuantity,
                maxQuantity,
                minItemQuantity,
                maxItemQuantity,
                page, size
        );
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the unique identifier of the order to retrieve
     * @return a {@code ResponseEntity} containing the requested {@code Order} if found,
     *         or appropriate error response if the order does not exist
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    ResponseEntity<Order> createNewOrder(@RequestBody List<Product> products){
        return orderService.createNewOrder(products);
    }

    /**
     * Deletes an order with the specified ID.
     *
     * @param id the unique identifier of the order to be deleted
     */
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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
    @PatchMapping(value = "/{id}/confirm", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<String> confirmOrder(@PathVariable(name = "id") Long orderId){
        return orderService.confirmOrder(orderId);
    }

    /**
     * Cancels an order by its unique identifier.
     *
     * @param orderId the unique identifier of the order to be canceled
     * @return a ResponseEntity containing a confirmation message for the canceled order
     */
    @PatchMapping(value = "/{id}/cancel", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<String> cancelOrder(@PathVariable(name = "id") Long orderId){
        return orderService.cancelOrder(orderId);
    }

    /**
     * Updates the status of an order to "delivered" based on the provided order ID.
     *
     * @param orderId the ID of the order to be marked as delivered
     * @return a ResponseEntity containing a message indicating the result of the operation
     */
    @PatchMapping(value = "/{id}/deliver", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<String> deliverOrder(@PathVariable(name = "id") Long orderId){
        return orderService.deliverOrder(orderId);
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

    @GetMapping("/customer-orders")
    Page<Order> getCustomerOrders(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return orderService.getCustomerOrders(Pageable.ofSize(size).withPage(page));
    }
}
