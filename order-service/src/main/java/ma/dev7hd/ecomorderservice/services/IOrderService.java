package ma.dev7hd.ecomorderservice.services;

import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.entities.ProductItem;
import ma.dev7hd.ecomorderservice.enums.OrderState;
import ma.dev7hd.ecomorderservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    ResponseEntity<Page<Order>> getOrders(String id,
                                          OrderState orderState,
                                          Double minPrice,
                                          Double maxPrice,
                                          Integer minQuantity,
                                          Integer maxQuantity,
                                          Integer minItemQuantity,
                                          Integer maxItemQuantity,
                                          int page,
                                          int size);

    ResponseEntity<Order> getOrder(Long id);

    ResponseEntity<Order> createNewOrder(List<Product> products);

    void deleteOrder(Long id);

    ResponseEntity<String> confirmOrder(Long orderId);

    ResponseEntity<String> cancelOrder(Long orderId);

    ResponseEntity<String> deliverOrder(Long orderId);

    List<ProductItem> getProductItems();

    Page<Order> getCustomerOrders(Pageable pageable);
}
