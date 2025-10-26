package com.springboot.mongo.restapi.repository.custom;

import com.springboot.mongo.restapi.dto.PagedResponse;
import com.springboot.mongo.restapi.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductRepositoryCustom {
    List<Product> filterProducts(String category, Double minPrice, Double maxPrice);
    List<Map<String, Object>> getAveragePricePerCategory();
    PagedResponse<Product> getPaginatedAndSorted(Pageable pageable);
}