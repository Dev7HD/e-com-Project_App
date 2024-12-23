package ma.dev7hd.ecominventoryservice.repositories;

import ma.dev7hd.ecominventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
