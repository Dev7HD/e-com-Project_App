package ma.dev7hd.ecomorderservice.services;

import lombok.AllArgsConstructor;
import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.entities.ProductItem;
import ma.dev7hd.ecomorderservice.enums.OrderState;
import ma.dev7hd.ecomorderservice.feignClients.InventoryClient;
import ma.dev7hd.ecomorderservice.models.Product;
import ma.dev7hd.ecomorderservice.repositories.OrderRepository;
import ma.dev7hd.ecomorderservice.repositories.ProductItemsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductItemsRepository productItemsRepository;
    private final InventoryClient inventoryClient;

    /**
     * Retrieves a paginated list of orders based on various filtering criteria.
     *
     * @param id The identifier of the order(s) to retrieve, or null to retrieve orders without filtering by ID.
     * @param orderState The state of the order(s) to filter by, or null to retrieve orders without filtering by state.
     * @param minPrice The minimum price of the order(s) to retrieve, or null for no minimum price filter.
     * @param maxPrice The maximum price of the order(s) to retrieve, or null for no maximum price filter.
     * @param minQuantity The minimum total quantity of items in the order(s) to retrieve, or null for no minimum quantity filter.
     * @param maxQuantity The maximum total quantity of items in the order(s) to retrieve, or null for no maximum quantity filter.
     * @param minItemQuantity The minimum quantity of a single item in the order(s) to retrieve, or null for no minimum item quantity filter.
     * @param maxItemQuantity The maximum quantity of a single item in the order(s) to retrieve, or null for no maximum item quantity filter.
     * @param page The page number to retrieve, starting from 0.
     * @param size The number of orders to retrieve per page.
     * @return A ResponseEntity containing a Page of orders matching the filter criteria, or a bad request response if the filters are invalid.
     */
    @Override
    public ResponseEntity<Page<Order>> getOrders(
            String id,
            OrderState orderState,
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Integer minItemQuantity,
            Integer maxItemQuantity,
            int page,
            int size
    ){
        boolean hasInvalidValues = isValidPrice(minPrice, maxPrice)
                || isValidQuantity(minQuantity, maxQuantity)
                || isValidQuantity(minItemQuantity, maxItemQuantity);

        if (hasInvalidValues) {
            return ResponseEntity.badRequest().build();
        }

        Page<Order> orders = orderRepository.getOrdersByCriteria(
                id,
                orderState,
                minPrice,
                maxPrice,
                minQuantity,
                maxQuantity,
                minItemQuantity,
                maxItemQuantity,
                Pageable.ofSize(size).withPage(page)
        );
        return ResponseEntity.ok(orders);
    }

    /**
     * Validates whether the given price range is valid.
     *
     * @param min the minimum price value to be validated
     * @param max the maximum price value to be compared against the minimum
     * @return true if both min and max are not null and min is greater than max, false otherwise
     */
    private boolean isValidPrice(Double min, Double max){
        return min != null && max != null && min > max;
    }

    /**
     * Checks if the provided quantity range is valid.
     *
     * @param min the minimum quantity, must not be null
     * @param max the maximum quantity, must not be null
     * @return true if the minimum quantity is greater than the maximum quantity, false otherwise
     */
    private boolean isValidQuantity(Integer min, Integer max){
        return min != null && max != null && min > max;
    }

    /**
     * Retrieves an order based on the provided order ID.
     *
     * @param id the ID of the order to be retrieved
     * @return a ResponseEntity containing the order if found, or a 404 Not Found status if no order exists with the provided ID
     */
    @Override
    public ResponseEntity<Order> getOrder(Long id){
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null){
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new order by saving it to the database, deducting product quantities from inventory,
     * and persisting individual product items associated with the order.
     *
     * @param products A list of {@code Product} objects representing the products to be included in the order.
     *                 Each product contains information such as its ID, price, and quantity.
     * @return A {@code ResponseEntity<Order>} containing the created {@code Order} object if successful.
     */
    @Override
    public ResponseEntity<Order> createNewOrder(List<Product> products){
        String currentCustomerName = getCurrentCustomerName();
        Order order = Order.builder()
                .customerName(currentCustomerName)
                .totalItems(products.size())
                .totalPrice(0)
                .totalQuantity(0)
                .build();
        products.forEach(product -> {
            order.setTotalPrice(order.getTotalPrice() + product.getPrice() * product.getQuantity());
            order.setTotalQuantity(order.getTotalQuantity() + product.getQuantity());
        });

        Order savedOrder = orderRepository.save(order);

        List<ProductItem> productItems = new ArrayList<>();
        products.parallelStream().forEach(product -> {
            Integer productQuantity = inventoryClient.decrementProductQuantity(product.getId(), product.getQuantity());
            if (productQuantity != null) {
                ProductItem productItem = ProductItem.builder()
                        .order(savedOrder)
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .productId(product.getId())
                        .build();
                productItems.add(productItem);
            } else {
                System.out.println("Error: Product with id '" + product.getId() + "' does not exist or has insufficient quantity.");
                throw new RuntimeException("Error: Product with id '" + product.getId() + "' does not exist or has insufficient quantity.");
            }
        });
        productItemsRepository.saveAll(productItems);
        return ResponseEntity.ok(savedOrder);
    }

    private String getCurrentCustomerName() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaims().get("preferred_username").toString();
    }

    /**
     * Deletes an order with the specified ID from the database.
     *
     * @param id the unique identifier of the order to be deleted
     */
    @Override
    public void deleteOrder(Long id){
        orderRepository.deleteById(id);
    }

    /**
     * Confirms an order by updating its state to PROCESSING if it is found and currently in the NEW state.
     *
     * @param orderId The unique identifier of the order to confirm.
     * @return A {@code ResponseEntity<String>} containing:
     *         - A success message with HTTP 200 status if the order is successfully confirmed.
     *         - An error message with HTTP 400 status if the order is not found or is not in the NEW state.
     */
    @Override
    public ResponseEntity<String> confirmOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null && order.getOrderState() == OrderState.NEW){
            order.setOrderState(OrderState.PROCESSING);
            orderRepository.save(order);
            return ResponseEntity.ok("Order confirmed.");
        } else {
            return ResponseEntity.badRequest().body("Order with id '" + orderId + "' was not found or is not in NEW state.");
        }
    }

    /**
     * Cancels the order with the given order ID if it exists and is in a valid state for cancellation.
     * An order can only be cancelled if it is not already in the DELIVERED or CANCELLED state.
     *
     * @param orderId the unique identifier of the order to be cancelled
     * @return a ResponseEntity containing a success message if the order was cancelled,
     *         or a bad request message if the order does not exist or cannot be cancelled
     */
    @Override
    public ResponseEntity<String> cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null && order.getOrderState() != OrderState.DELIVERED && order.getOrderState() != OrderState.CANCELLED){
            order.getProductItems().forEach(productItem -> inventoryClient.incrementProductQuantity(productItem.getProductId(), productItem.getQuantity()));
            order.setOrderState(OrderState.CANCELLED);
            orderRepository.save(order);
            return ResponseEntity.ok("Order cancelled.");
        } else {
            return ResponseEntity.badRequest().body("Order with id '" + orderId + "' was not found or is already in DELIVERED or CANCELLED state.");
        }
    }

    /**
     * Handles the delivery of an order by updating its state to DELIVERED, if applicable.
     *
     * @param orderId the ID of the order to be delivered
     * @return ResponseEntity with a success message if the order was successfully delivered,
     *         or an error message if the order is not found, is canceled, or has already been delivered
     */
    @Override
    public ResponseEntity<String> deliverOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getOrderState() == OrderState.DELIVERED){
            return ResponseEntity.badRequest().body("Order with id '" + orderId + "' was already delivered.");
        } else if (order != null && order.getOrderState() == OrderState.CANCELLED){
            return ResponseEntity.badRequest().body("Order with id '" + orderId + "' was cancelled.");
        } else if (order != null && order.getOrderState() == OrderState.PROCESSING) {
            order.setOrderState(OrderState.DELIVERED);
            orderRepository.save(order);
            return ResponseEntity.ok("Order delivered.");
        }
        return ResponseEntity.badRequest().body("Order with id '" + orderId + "' was not found or is not in PROCESSING state.");
    }

    @Override
    public List<ProductItem> getProductItems(){
        return productItemsRepository.findAll();
    }

    @Override
    public Page<Order> getCustomerOrders(Pageable pageable){
        String currentCustomerName = getCurrentCustomerName();
        return orderRepository.findByCustomerName(currentCustomerName, pageable);
    }
}
