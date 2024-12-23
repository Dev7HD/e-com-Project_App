package ma.dev7hd.ecomorderservice.repositories;

import ma.dev7hd.ecomorderservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemsRepository extends JpaRepository<ProductItem, Long> {
}
