

A production-ready RESTful API built with Spring Boot and MongoDB, featuring CRUD operations, filtering, pagination, validation, and input sanitization.

- Java 17
- Spring Boot 3.5
- MongoDB

See API reference here: [API_REFERENCE.md](API_REFERENCE.md)

## Quick start

### Prerequisites
- Java 17+
- Maven 3.9+
- A MongoDB connection (local or Atlas)

### Configure
The app reads Mongo connection settings from environment variables (via spring-dotenv, .env supported):

```
MONGODB_URI=mongodb://localhost:27017
MONGODB_DBNAME=productsdb
```

You can set them in your shell or create a `.env` file in the project root. The following Spring properties are used (see src/main/resources/application.properties):

```
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=${MONGODB_DBNAME}
```

### Run

- Using Maven:
  - Development: `mvn spring-boot:run`
  - Package JAR: `mvn clean package`
  - Run JAR: `java -jar target/springboot-mongo-rest-api-0.0.1-SNAPSHOT.jar`

Default base URL: `http://localhost:8080`

API base path: `/api/v1`

Example health check: `GET http://localhost:8080/api/v1/products/health`

### Sample requests

- Create product
```
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
        "name":"Keyboard",
        "description":"Mechanical",
        "price":59.99,
        "quantity":10,
        "category":"Peripherals"
      }'
```

- Get paginated list
```
curl "http://localhost:8080/api/v1/products/list?page=0&size=5&sortBy=name&order=ASC"
```

- Filter products
```
curl "http://localhost:8080/api/v1/products/filter?category=Peripherals&minPrice=10&maxPrice=200"
```

## Project layout

- `src/main/java/com/springboot/mongo/restapi/controller/ProductController.java` — REST endpoints
- `src/main/java/com/springboot/mongo/restapi/service/ProductService.java` — business logic
- `src/main/java/com/springboot/mongo/restapi/repository` — Spring Data Mongo repositories
- `src/main/java/com/springboot/mongo/restapi/model/Product.java` — product entity and bean validation
- `src/main/java/com/springboot/mongo/restapi/exception/GlobalExceptionHandler.java` — error responses
- `src/main/java/com/springboot/mongo/restapi/util/ApiPaths.java` — centralized API base paths

## Tests
Run unit tests:
```
mvn test
```

## Notes
- Validation errors and bad requests are returned as JSON with helpful messages.
- Sorting fields are validated server-side; allowed fields match the Product properties.
- See full endpoint list and example responses in [API_REFERENCE.md](API_REFERENCE.md).
