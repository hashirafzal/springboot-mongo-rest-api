package com.springboot.mongo.restapi.util;

public final class ApiPaths {

    private ApiPaths() {} // prevent instantiation

    // API version in one place — easy to bump from v1 → v2 later
    public static final String VERSION = "/api/v1";

    // Grouped per domain
    public static final class Product {
        public static final String BASE = VERSION + "/products";
    }

    public static final class Category {
        public static final String BASE = VERSION + "/categories";
    }

    public static final class Order {
        public static final String BASE = VERSION + "/orders";
    }
}

