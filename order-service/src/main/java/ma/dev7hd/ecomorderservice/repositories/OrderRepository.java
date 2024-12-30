package ma.dev7hd.ecomorderservice.repositories;

import ma.dev7hd.ecomorderservice.entities.Order;
import ma.dev7hd.ecomorderservice.enums.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE " +
            "(:id IS NULL OR :id = '' OR LOWER(CAST(o.id AS STRING)) LIKE :id%) AND " +
            "(:orderState IS NULL OR o.orderState = :orderState) AND " +
            "(:minPrice IS NULL OR :minPrice <= 0 OR o.totalPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR :maxPrice <= 0 OR o.totalPrice <= :maxPrice) AND " +
            "(:minQuantity IS NULL OR :minQuantity < 0 OR o.totalQuantity >= :minQuantity) AND " +
            "(:maxQuantity IS NULL OR :maxQuantity < 0 OR o.totalQuantity <= :maxQuantity) AND " +
            "(:minItemQuantity IS NULL OR :minItemQuantity < 0 OR o.totalItems >= :minItemQuantity) AND " +
            "(:maxItemQuantity IS NULL OR :maxItemQuantity < 0 OR o.totalItems <= :maxItemQuantity) "
    )
    Page<Order> getOrdersByCriteria(
            @Param("id") String id,
            @Param("orderState") OrderState orderState,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            @Param("minItemQuantity") Integer minItemQuantity,
            @Param("maxItemQuantity") Integer maxItemQuantity,
            Pageable pageable);

    Page<Order> findByCustomerName(String customerName, Pageable pageable);
}
