package htwberlin.focustimer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import htwberlin.focustimer.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> { }