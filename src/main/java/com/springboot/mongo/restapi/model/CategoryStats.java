package com.springboot.mongo.restapi.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStats {
    private String category;
    private double averagePrice;
    private int totalQuantity;
}
