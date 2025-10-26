# API Reference — springboot-mongo-rest-api

Base URL: `http://localhost:8080`

API base path prefix: `/api/v1`

All responses are JSON. Request and response bodies shown below are examples; your actual IDs and timestamps will vary.

---

## Product endpoints
Base: `/api/v1/products`

### 1) GET `/api/v1/products`
Fetch all products.
- Response 200
```
[
  {
    "id": "6753ac2b6f1f5e3f3b33f8f1",
    "name": "Keyboard",
    "description": "Mechanical",
    "price": 59.99,
    "quantity": 10,
    "category": "Peripherals"
  }
]
```

### 2) GET `/api/v1/products/count`
Get total number of products.
- Response 200
```
{ "count": 42 }
```

### 3) POST `/api/v1/products`
Create a product.
- Body (application/json)
```
{
  "name": "Keyboard",
  "description": "Mechanical",
  "price": 59.99,
  "quantity": 10,
  "category": "Peripherals"
}
```
- Success 201 Created
  - Location header: `/api/v1/products/{id}`
  - Body: created product JSON
- Validation error 400
```
{
  "timestamp": "2025-10-27T01:29:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "name": "Product name is required"
  }
}
```

### 4) GET `/api/v1/products/category/{category}`
List products by category.
- Path variables are sanitized server-side.
- Response 200: array of Product

### 5) GET `/api/v1/products/price/greater/{price}`
List products with price greater than the given value.
- Response 200: array of Product

### 6) GET `/api/v1/products/search?name=...`
Case-insensitive search by name.
- Query params: `name` (required)
- Response 200: array of Product

### 7) GET `/api/v1/products/filter`
Filter by optional `category`, `minPrice`, `maxPrice`.
- Query params (all optional):
  - `category` (String)
  - `minPrice` (Number)
  - `maxPrice` (Number)
- Rules: `minPrice` cannot be greater than `maxPrice` (400 Bad Request)
- Responses:
  - 200 OK: array of Product
  - 400 Bad Request (IllegalArgumentException)
```
{ "error": "minPrice cannot be greater than maxPrice" }
```

### 8) GET `/api/v1/products/analytics/average`
Average price per category.
- Response 200
```
[
  { "category": "Peripherals", "averagePrice": 45.32 },
  { "category": "Laptops",     "averagePrice": 999.00 }
]
```

### 9) GET `/api/v1/products/list`
Paginated and sorted products.
- Query params:
  - `page` (default: 0)
  - `size` (default: 5)
  - `sortBy` (default: name) — validated
  - `order` (default: ASC) — `ASC` or `DESC`
- Response 200
```
{
  "content": [ { /* Product */ }, ... ],
  "page": 0,
  "size": 5,
  "totalElements": 42,
  "totalPages": 9,
  "sort": "name: ASC"
}
```

### 10) PUT `/api/v1/products/{id}`
Replace a product by ID.
- Body: full Product JSON (all fields required except id)
- Responses:
  - 200 OK: updated Product
  - 404 Not Found

### 11) PATCH `/api/v1/products/{id}`
Partially update a product.
- Body: partial fields supported: `name`, `description`, `price`, `quantity`, `category`
- Responses:
  - 200 OK: updated Product
  - 400 Bad Request when an unsupported field is passed
```
{ "error": "Invalid field: foo" }
```
  - 404 Not Found

### 12) GET `/api/v1/products/{id}`
Fetch product by ID.
- Responses:
  - 200 OK: Product
  - 404 Not Found

### 13) DELETE `/api/v1/products/{id}`
Delete product by ID.
- Responses:
  - 204 No Content
  - 404 Not Found

### 14) HEAD `/api/v1/products/{id}`
Check existence of a product by ID.
- Responses:
  - 200 OK (exists)
  - 404 Not Found (not exists)

### 15) GET `/api/v1/products/health`
Lightweight health check with DB status and response time.
- Response 200 when up, 503 when down
```
{
  "status": "UP",
  "database": "UP",
  "responseTimeMs": 12
}
```
Or when down:
```
{
  "status": "DOWN",
  "database": "DOWN",
  "responseTimeMs": 25,
  "error": "<message>"
}
```

---

## Data model

Product
```
{
  "id": "string",
  "name": "string (2-100 chars)",
  "description": "string (<=500 chars)",
  "price": number (>= 0.01),
  "quantity": integer (>= 0),
  "category": "string (2-50 chars)"
}
```

## Error format

- Validation errors (400):
```
{
  "timestamp": "2025-10-27T01:29:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "field": "message"
  }
}
```

- General server error (500):
```
{
  "timestamp": "2025-10-27T01:29:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "..."
}
```
