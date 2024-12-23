package ma.dev7hd.ecomorderservice.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.dev7hd.ecomorderservice.enums.OrderState;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "orders-table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductItem> productItem;

    @PrePersist
    void generateOrderDate() {
        // Generate Order date on saving to database if not provided.
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }

        // Generate Order state on saving to database if not provided.
        if (this.orderState == null) {
            this.orderState = OrderState.NEW;
        }
    }

}
