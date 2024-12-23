package ma.dev7hd.ecomorderservice.repositories;

import ma.dev7hd.ecomorderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
