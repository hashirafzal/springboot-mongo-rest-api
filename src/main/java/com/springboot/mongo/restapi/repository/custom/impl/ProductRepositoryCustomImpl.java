package com.springboot.mongo.restapi.repository.custom.impl;


import com.springboot.mongo.restapi.dto.PagedResponse;
import com.springboot.mongo.restapi.model.Product;
import com.springboot.mongo.restapi.repository.custom.ProductRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }



    @Override
    public List<Product> filterProducts(String category, Double minPrice, Double maxPrice) {
        Query query = new Query();
        Criteria criteria = new Criteria();
       if(category != null ) {
           criteria = criteria.and("category").is(category);
       }
       if(minPrice != null) {
           criteria = criteria.and("price").gte(minPrice);
       }
       if(maxPrice != null) {
           criteria = criteria.and("price").lte(maxPrice);
       }
       if(!criteria.getCriteriaObject().isEmpty()) {
           query.addCriteria(criteria);
           return mongoTemplate.find(query, Product.class);
       }


        return List.of();
    }

    // ✅ Aggregation: average price per category
    @Override
    public List<Map<String, Object>> getAveragePricePerCategory() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("category")
                        .avg("price").as("averagePrice")
                        .sum("quantity").as("totalQuantity"),
                Aggregation.sort(Sort.Direction.DESC, "averagePrice")
        );
        AggregationResults<Map> results =
                mongoTemplate.aggregate(aggregation, Product.class, Map.class);
        return  (List<Map<String, Object>>) (List<?>)  results.getMappedResults();
    }

    public PagedResponse<Product> getPaginatedAndSorted(Pageable pageable) {
        Query query = new Query();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            String sortBy = order.getProperty();
            Sort.Direction direction = order.getDirection();
            query.with(Sort.by(direction, sortBy));
        } else {
            // Default sort if none provided
            query.with(Sort.by(Sort.Direction.ASC, "name"));
        }

        long totalElements = mongoTemplate.count(query, Product.class);

        // Apply pagination
        query.skip((long) page * size);
        query.limit(size);

        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PagedResponse<>(products,page,size,totalElements);
    }
}
