package htwberlin.focustimer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    ProductService service;

    Logger logger = LoggerFactory.getLogger(ShopController.class);

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return service.save(product);
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) {
        logger.info("GET request on route things with {}", id);
        Long productId = Long.parseLong(id);
        return service.get(productId);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAll();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        Long productId = Long.parseLong(id);
        
        // Überprüfen, ob das Produkt existiert, bevor du versuchst, es zu löschen
        if (service.get(productId) != null) {
            service.delete(productId);
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}