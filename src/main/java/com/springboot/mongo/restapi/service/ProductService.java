package com.springboot.mongo.restapi.service;

import com.springboot.mongo.restapi.dto.PagedResponse;
import com.springboot.mongo.restapi.model.Product;
import com.springboot.mongo.restapi.repository.ProductRepository;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log
public class ProductService {

    private final ProductRepository repository;
    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public long count() {
        log.info("Counting all products");
        return repository.count();
    }

    public List<Product> findAll()
    {
        log.info("Finding all products");
        List<Product> products = repository.findAll();
        log.info(() -> "Fetched " + products.size() + " products");
        return products;
    }

    public Product save(Product product) {
        log.info(() -> "Saving new product: " + product.getName());
        Product saved = repository.save(product);
        log.info(() -> "Saved product with ID: " + saved.getId());
        return saved;
    }

    public List<Product> findByCategory(String category) {
        log.info(() -> "Finding products by category: " + category);
        List<Product> result = repository.findByCategory(category);
        log.info(() -> "Found " + result.size() + " products in category: " + category);
        return result;
    }

    public List<Product> findByPriceGreaterThan(double price) {
        log.info(() -> "Finding products with price greater than: " + price);
        List<Product> result = repository.findByPriceGreaterThan(price);
        log.info(() -> "Found " + result.size() + " products with price > " + price);
        return result;
    }

    public List<Product> findByNameContainingIgnoreCase(String name) {
        log.info(() -> "Searching products by name (case-insensitive): " + name);
        List<Product> result = repository.findByNameContainingIgnoreCase(name);
        log.info(() -> "Found " + result.size() + " products matching: " + name);
        return result;
    }

    public List<Product> filterProducts(String category, Double minPrice, Double maxPrice) {
        log.info(() -> "Filtering products | Category: " + category + " | MinPrice: " + minPrice + " | MaxPrice: " + maxPrice);

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            log.warning("Invalid filter: minPrice > maxPrice");
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }

        List<Product> filtered = repository.filterProducts(category, minPrice, maxPrice);
        log.info(() -> "Filtered result size: " + filtered.size());
        return filtered;
    }


    public List<Map<String, Object>> getAveragePricePerCategory() {
        log.info("Calculating average price per category");
        List<Map<String, Object>> averages = repository.getAveragePricePerCategory();
        log.info(() -> "Found averages for " + averages.size() + " categories");
        return averages;
    }
    public PagedResponse<Product> findAllPaginatedAndSorted(Pageable pageable) {
        log.info(() -> "Fetching paginated products | Page: " + pageable.getPageNumber() + " | Size: " + pageable.getPageSize() + " | Sort: " + pageable.getSort());
        return repository.getPaginatedAndSorted(pageable);
    }

    public Optional<Product> updateProduct(String id, Product updatedProduct) {
        log.info(() -> "Updating product with ID: " + id);
        return repository.findById(id).map(existing -> {
            log.info(() -> "Product found for update: " + existing.getName());
            // Merge fields from updatedProduct into existing entity
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setQuantity(updatedProduct.getQuantity());
            existing.setCategory(updatedProduct.getCategory());

            // Ensure the ID remains the same as path variable
            existing.setId(id);

            Product saved = repository.save(existing);
            log.info(() -> "Updated product with ID: " + saved.getId());
            return saved;
        });
    }

    public Optional<Product> patchProduct(String id, Map<String, Object> updates) {
        log.info(() -> "Patching product ID: " + id + " with fields: " + updates.keySet());
        return repository.findById(id).map(existingProduct -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> existingProduct.setName((String) value);
                    case "description" -> existingProduct.setDescription((String) value);
                    case "price" -> existingProduct.setPrice(Double.parseDouble(value.toString()));
                    case "quantity" -> existingProduct.setQuantity((Integer) value);
                    case "category" -> existingProduct.setCategory((String) value);
                    default -> {
                        log.warning("Invalid field in patch: " + key);
                        throw new IllegalArgumentException("Invalid field: " + key);
                    }
                }
            });
            Product saved = repository.save(existingProduct);
            log.info(() -> "Patched product saved with ID: " + saved.getId());
            return saved;
        });
    }

    public Optional<Product> findById(String id) {
        log.info(() -> "Fetching product by ID: " + id);
        return repository.findById(id);
    }

    public boolean existsById(String id) {
        log.info(() -> "Checking existence for product ID: " + id);
        return repository.existsById(id);
    }

    public Map<String, Object> healthCheck() {
        long start = System.currentTimeMillis();
        try {
            // lightweight check
            repository.count();
            long duration = System.currentTimeMillis() - start;
            return Map.of(
                    "status", "UP",
                    "database", "UP",
                    "responseTimeMs", duration
            );
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            return Map.of(
                    "status", "DOWN",
                    "database", "DOWN",
                    "responseTimeMs", duration,
                    "error", e.getMessage()
            );
        }
    }

    public boolean deleteProduct(String id) {
        log.info(() -> "Deleting product with ID: " + id);
        return repository.findById(id).map(entity -> {
            repository.deleteById(id);
            log.info(() -> "Deleted product with ID: " + id);
            return true;
        }).orElseGet(() -> {
            log.warning("Product not found for deletion: " + id);
            return false;
        });
    }

    public List<Product> saveAll(List<Product> products) {
        log.info(() -> "Saving " + products.size() + " products in bulk");
        List<Product> saved = repository.saveAll(products);
        log.info(() -> "Bulk save completed. Total saved: " + saved.size());
        return saved;
    }
}
