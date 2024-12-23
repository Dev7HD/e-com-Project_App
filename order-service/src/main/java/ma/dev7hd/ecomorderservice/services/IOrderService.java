package ma.dev7hd.ecomorderservice.services;

import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.entities.ProductItem;
import ma.dev7hd.ecomorderservice.models.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    ResponseEntity<List<Order>> getOrders();

    ResponseEntity<Order> getOrder(Long id);

    ResponseEntity<Order> createNewOrder(List<Product> products);

    void deleteOrder(Long id);

    ResponseEntity<String> confirmOrder(Long orderId);

    ResponseEntity<String> cancelOrder(Long orderId);

    ResponseEntity<String> deliverOrder(Long orderId);

    void printProducts();

    List<ProductItem> getProductItems();
}
