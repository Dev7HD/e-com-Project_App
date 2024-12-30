package ma.dev7hd.ecomorderservice.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.dev7hd.ecomorderservice.enums.OrderState;

import java.util.Date;
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
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    private double totalPrice;

    private int totalQuantity;

    private int totalItems;

    private String customerName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductItem> productItems;

    @PrePersist
    void generateOrderDate() {
        // Generate Order date on saving to database if not provided.
        if (this.orderDate == null) {
            this.orderDate = new Date();
        }

        // Generate Order state on saving to database if not provided.
        if (this.orderState == null) {
            this.orderState = OrderState.NEW;
        }
    }

}
