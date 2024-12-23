package ma.dev7hd.ecomorderservice.models;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @ToString
public class Product {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
