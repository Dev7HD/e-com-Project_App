package ma.dev7hd.ecominventoryservice;

import ma.dev7hd.ecominventoryservice.entities.Product;
import ma.dev7hd.ecominventoryservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    //@Bean
    CommandLineRunner commandLineRunner(ProductRepository productRepository) {
        return args -> {
            Product product1 = new Product();
            product1.setName("MacBook Pro M4");
            product1.setDescription("MacBook Pro M4, Silver, 16 Gb Ram, 1To SSD NVME");
            product1.setPrice(16000.00);
            product1.setQuantity(3);

            Product product2 = new Product();
            product2.setName("Razer Blade 15");
            product2.setDescription("Razer Blade 15, Black, core i9 149900HQ, 32 Gb Ram, 2To SSD NVME");
            product2.setPrice(25000.00);
            product2.setQuantity(5);

            Product product3 = new Product();
            product3.setName("PS5 Pro");
            product3.setDescription("PS5 Pro, 2To SSD NVME");
            product3.setPrice(8500.00);
            product3.setQuantity(15);

            Product product4 = new Product();
            product4.setName("Asus Predator 21");
            product4.setDescription("Asus Predator 21, Black, i7 13700k, 64 Gb Ram, 2To SSD NVME, dual RTX 4080");
            product4.setPrice(30000.00);
            product4.setQuantity(3);

            List<Product> products = List.of(product1, product2, product3, product4);
            productRepository.saveAll(products);
        };
    }

}
