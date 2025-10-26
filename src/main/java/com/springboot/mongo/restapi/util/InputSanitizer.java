package com.springboot.mongo.restapi.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for sanitizing and validating user inputs
 * Prevents injection attacks and ensures data integrity
 */
@Component
public class InputSanitizer {

    // Maximum allowed lengths
    private static final int MAX_SEARCH_LENGTH = 100;
    private static final int MAX_STRING_LENGTH = 500;
    private static final int MAX_CATEGORY_LENGTH = 50;

    // Price constraints
    private static final double MIN_PRICE = 0.01;
    private static final double MAX_PRICE = 999999.99;

    // Quantity constraints
    private static final int MIN_QUANTITY = 0;
   // private static final int MAX_QUANTITY = 100000;

    /**
     * Sanitize search input to prevent injection attacks
     * Removes special characters that could be used in NoSQL injection
     *
     * @param input Search term from user
     * @return Sanitized search string
     */
    public String sanitizeSearch(String input) {
        if (input == null) {
            return "";
        }

        // Trim whitespace
        input = input.trim();

        // Check if empty after trimming
        if (input.isEmpty()) {
            return "";
        }

        // Limit length to prevent resource exhaustion
        if (input.length() > MAX_SEARCH_LENGTH) {
            input = input.substring(0, MAX_SEARCH_LENGTH);
        }

        // Remove potentially dangerous characters for MongoDB regex
        // These characters have special meaning in regex: $ { } [ ] ( ) * + ? | ^ \
        input = input.replaceAll("[\\$\\{\\}\\[\\]\\(\\)\\*\\+\\?\\|\\^\\\\]", "");

        // Remove leading/trailing dots (can cause issues in some queries)
        input = input.replaceAll("^\\.+|\\.+$", "");

        return input;
    }

    /**
     * Sanitize general string input (for names, descriptions, categories)
     *
     * @param input String from user
     * @return Sanitized string
     * @throws IllegalArgumentException if input exceeds maximum length
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }

        // Trim whitespace
        input = input.trim();

        // Check if empty after trimming
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty or only whitespace");
        }

        // Enforce maximum length
        if (input.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException(
                    "Input exceeds maximum length of " + MAX_STRING_LENGTH + " characters"
            );
        }

        // Remove control characters (non-printable characters)
        input = input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        return input;
    }

    /**
     * Sanitize category input
     *
     * @param category Category name from user
     * @return Sanitized category
     * @throws IllegalArgumentException if invalid
     */
    public String sanitizeCategory(String category) {
        if (category == null) {
            return null;
        }

        category = category.trim();

        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        if (category.length() > MAX_CATEGORY_LENGTH) {
            throw new IllegalArgumentException(
                    "Category exceeds maximum length of " + MAX_CATEGORY_LENGTH + " characters"
            );
        }

        // Only allow letters, numbers, spaces, hyphens, and underscores
        if (!category.matches("^[a-zA-Z0-9\\s\\-_]+$")) {
            throw new IllegalArgumentException(
                    "Category can only contain letters, numbers, spaces, hyphens, and underscores"
            );
        }

        return category;
    }

    /**
     * Validate and sanitize price input
     *
     * @param price Price value from user
     * @return Sanitized price rounded to 2 decimal places
     * @throws IllegalArgumentException if price is invalid
     */
    public Double sanitizePrice(Double price) {
        if (price == null) {
            return null;
        }

        // Check for NaN or Infinity
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("Invalid price value");
        }

        // Check minimum price
        if (price < MIN_PRICE) {
            throw new IllegalArgumentException(
                    "Price must be at least " + MIN_PRICE
            );
        }

        // Check maximum price
        if (price > MAX_PRICE) {
            throw new IllegalArgumentException(
                    "Price cannot exceed " + MAX_PRICE
            );
        }

        // Round to 2 decimal places (standard for currency)
        return Math.round(price * 100.0) / 100.0;
    }

    /**
     * Validate and sanitize quantity input
     *
     * @param quantity Quantity value from user
     * @return Validated quantity
     * @throws IllegalArgumentException if quantity is invalid
     */
    public Integer sanitizeQuantity(Integer quantity) {
        if (quantity == null) {
            return null;
        }

        // Check minimum quantity
        if (quantity < MIN_QUANTITY) {
            throw new IllegalArgumentException(
                    "Quantity cannot be less than " + MIN_QUANTITY
            );
        }

        // Check maximum quantity , commented, can be uncommented if needed
//        if (quantity > MAX_QUANTITY) {
//            throw new IllegalArgumentException(
//                    "Quantity cannot exceed " + MAX_QUANTITY
//            );
//        }

        return quantity;
    }

    /**
     * Sanitize product name
     * Product names have stricter requirements than general strings
     *
     * @param name Product name from user
     * @return Sanitized name
     * @throws IllegalArgumentException if name is invalid
     */
    public String sanitizeProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        name = name.trim();

        // Product names should be between 2 and 100 characters
        if (name.length() < 2) {
            throw new IllegalArgumentException("Product name must be at least 2 characters");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name cannot exceed 100 characters");
        }

        // Remove control characters
        name = name.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        return name;
    }

    /**
     * Sanitize description
     *
     * @param description Product description from user
     * @return Sanitized description
     */
    public String sanitizeDescription(String description) {
        if (description == null) {
            return null;
        }

        description = description.trim();

        if (description.isEmpty()) {
            return null; // Empty descriptions are allowed
        }

        // Descriptions can be longer
        if (description.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException(
                    "Description exceeds maximum length of " + MAX_STRING_LENGTH + " characters"
            );
        }

        // Remove control characters except newlines and tabs
        description = description.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        return description;
    }

    /**
     * Validate pagination parameters
     *
     * @param page Page number
     * @param size Page size
     * @throws IllegalArgumentException if parameters are invalid
     */
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }

        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }
    }

    /**
     * Validate sort field name
     * Only allows whitelisted field names to prevent injection
     *
     * @param sortBy Field name to sort by
     * @return Validated sort field
     * @throws IllegalArgumentException if field is not allowed
     */
    public String validateSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "name"; // Default sort field
        }

        // Whitelist of allowed sort fields
        String[] allowedFields = {"id", "name", "price", "quantity", "category", "description"};

        for (String allowedField : allowedFields) {
            if (allowedField.equalsIgnoreCase(sortBy.trim())) {
                return allowedField;
            }
        }

        throw new IllegalArgumentException(
                "Invalid sort field. Allowed fields: id, name, price, quantity, category, description"
        );
    }

    /**
     * Validate sort order
     *
     * @param order Sort order (ASC or DESC)
     * @return Validated sort order
     * @throws IllegalArgumentException if order is invalid
     */
    public String validateSortOrder(String order) {
        if (order == null) {
            return "ASC"; // Default
        }

        String upperOrder = order.trim().toUpperCase();

        if (upperOrder.equals("ASC") || upperOrder.equals("DESC")) {
            return upperOrder;
        }

        throw new IllegalArgumentException("Sort order must be either ASC or DESC");
    }

    /**
     * Sanitize ID parameter (MongoDB ObjectId)
     *
     * @param id ID from user
     * @return Sanitized ID
     * @throws IllegalArgumentException if ID format is invalid
     */
    public String sanitizeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID is required");
        }

        id = id.trim();

        // MongoDB ObjectId is 24 hex characters
        if (id.length() != 24) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        // Check if it's a valid hex string
        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return id;
    }

    // Getter methods for constants (useful for displaying in error messages)

    public int getMaxSearchLength() {
        return MAX_SEARCH_LENGTH;
    }

    public int getMaxStringLength() {
        return MAX_STRING_LENGTH;
    }

    public int getMaxCategoryLength() {
        return MAX_CATEGORY_LENGTH;
    }

    public double getMinPrice() {
        return MIN_PRICE;
    }

    public double getMaxPrice() {
        return MAX_PRICE;
    }

    public int getMinQuantity() {
        return MIN_QUANTITY;
    }

//    public int getMaxQuantity() {
//        return MAX_QUANTITY;
//    }
}