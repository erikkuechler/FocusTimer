package htwberlin.focustimer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import htwberlin.focustimer.entity.Product;
import htwberlin.focustimer.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository repo;

    public Product save(Product product) {
        return repo.save(product);
    }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException());
    }

    public List<Product> getAll() {
        Iterable<Product> iterator = repo.findAll();
        List<Product> products = new ArrayList<Product>();
        for (Product product : iterator)  products.add(product);
        return products;
    }
}