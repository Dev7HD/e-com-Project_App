package ma.dev7hd.ecominventoryservice.repositories;

import ma.dev7hd.ecominventoryservice.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Retrieves a paginated list of products based on the specified search criteria.
     *
     * @param id the unique identifier of the product, can be partial. Pass null or empty to ignore this filter.
     * @param name the name of the product, supports partial matches. Pass null or empty to ignore this filter.
     * @param description the description of the product, supports partial matches. Pass null or empty to ignore this filter.
     * @param minPrice the minimum price filter for the product. Pass null to ignore this filter.
     * @param maxPrice the maximum price filter for the product. Pass null to ignore this filter.
     * @param minQuantity the minimum quantity filter for the product. Pass null to ignore this filter.
     * @param maxQuantity the maximum quantity filter for the product. Pass null to ignore this filter.
     * @param pageable the pagination and sorting information.
     * @return a page of products matching the specified criteria.
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(:id IS NULL OR LOWER(CAST(p.id AS STRING)) LIKE :id%) AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE %:name%) AND " +
            "(:description IS NULL OR LOWER(p.description) LIKE %:description%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minQuantity IS NULL OR p.quantity >= :minQuantity) AND " +
            "(:maxQuantity IS NULL OR p.quantity <= :maxQuantity) ")
    Page<Product> getProductsByCriteria(
            @Param("id") String id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            Pageable pageable);
}
