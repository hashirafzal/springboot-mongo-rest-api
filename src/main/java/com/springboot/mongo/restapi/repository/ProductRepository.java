package com.springboot.mongo.restapi.repository;

import com.springboot.mongo.restapi.model.Product;
import com.springboot.mongo.restapi.repository.custom.ProductRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product,String>, ProductRepositoryCustom {

    // ✅ Find by exact match
    List<Product> findByCategory(String category);

    // ✅ Find by price greater than
    List<Product> findByPriceGreaterThan(double price);

    // ✅ Find by price less than
    List<Product> findByPriceLessThan(double price);

    // ✅ Find by category and quantity greater than
    List<Product> findByCategoryAndQuantityGreaterThan(String category, int quantity);

    // ✅ Find by name containing (case-insensitive)
    @Query("{ 'name' : { $regex: ?0, $options: 'i' } }")
    List<Product> findByNameContainingIgnoreCase(String name);
}
