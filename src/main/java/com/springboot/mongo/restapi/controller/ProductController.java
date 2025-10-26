package com.springboot.mongo.restapi.controller;

import com.springboot.mongo.restapi.dto.PagedResponse;
import com.springboot.mongo.restapi.model.Product;
import com.springboot.mongo.restapi.service.ProductService;
import com.springboot.mongo.restapi.util.ApiPaths;
import com.springboot.mongo.restapi.util.InputSanitizer;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping(ApiPaths.Product.BASE)
public class ProductController {


    private final ProductService productService;
    private final InputSanitizer inputSanitizer;

    public ProductController(ProductService productService, InputSanitizer inputSanitizer) {

        this.productService = productService;
        this.inputSanitizer = inputSanitizer;
    }
    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countProducts() {
        long count = productService.count();
        return ResponseEntity.ok(Map.of("count", count));
    }




    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product product)
    {
        Product saved = productService.save(product);
        URI location = URI.create(ApiPaths.Product.BASE + "/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }


    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productService.findByCategory(category);
    }

    @GetMapping("/price/greater/{price}")
    public List<Product> getByPriceGreater(@Valid @PathVariable double price) {
        return productService.findByPriceGreaterThan(price);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        String sanitizedString  = inputSanitizer.sanitizeString(name);



        return ResponseEntity.ok(productService.findByNameContainingIgnoreCase(sanitizedString));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        // Sanitize inputs
        if (category != null) {
            category = inputSanitizer.sanitizeCategory(category);
        }

        if (minPrice != null) {
            minPrice = inputSanitizer.sanitizePrice(minPrice);
        }

        if (maxPrice != null) {
            maxPrice = inputSanitizer.sanitizePrice(maxPrice);
        }

        return ResponseEntity.ok(productService.filterProducts(category, minPrice, maxPrice));
    }

    @GetMapping("/analytics/average")
    public ResponseEntity<List<Map<String, Object>>> getAverageByCategory() {
        return ResponseEntity.ok(productService.getAveragePricePerCategory());
    }


    @GetMapping("/list")
    public ResponseEntity<PagedResponse<Product>> getProductsPagedSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String order
    ) {
        inputSanitizer.validatePagination(page, size);

        String validatedSortBy = inputSanitizer.validateSortField(sortBy);
        String validatedOrder = inputSanitizer.validateSortOrder(order);

        Sort sort = validatedOrder.equalsIgnoreCase("ASC") ?
                Sort.by(validatedSortBy).ascending() :
                Sort.by(validatedSortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.findAllPaginatedAndSorted(pageable));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody Product updatedProduct
    ) {
        String sanitizedId = inputSanitizer.sanitizeId(id);

        return productService.updateProduct(sanitizedId, updatedProduct)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProduct(
            @PathVariable String id,
            @Valid @RequestBody Map<String, Object> updates) {

        String sanitizedId = inputSanitizer.sanitizeId(id);
        return productService.patchProduct(sanitizedId, updates)
                .map(ResponseEntity::ok) // 200 OK with product
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 if not found
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        String sanitizedId = inputSanitizer.sanitizeId(id);

        return productService.findById(sanitizedId).map(ResponseEntity::ok)//
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct( @PathVariable String id) {
        String sanitizedId = inputSanitizer.sanitizeId(id);

        return productService.deleteProduct(sanitizedId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> exists(@PathVariable String id) {
        String sanitizedId = inputSanitizer.sanitizeId(id);
        return productService.existsById(sanitizedId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = productService.healthCheck();
        HttpStatus code = "UP".equals(status.get("status")) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(code).body(status);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> saveAll( @Valid @RequestBody List<Product> products) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveAll(products));
    }
}
