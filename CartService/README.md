# Cart Service

This microservice is part of the e-commerce system and handles shopping cart management. It provides functionality for creating and managing shopping carts, adding/removing items, and handling cart expiration.

## Features

- Create and manage shopping carts
- Add, update, and remove cart items
- Cart expiration handling
- Redis caching for improved performance
- Event publishing via RabbitMQ
- OpenAPI documentation
- Comprehensive error handling
- Transaction management

## Technical Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL Database
- Redis Cache
- RabbitMQ
- OpenAPI/Swagger
- Maven

## API Endpoints

### Cart Management
- `GET /api/v1/carts/user/{userId}` - Get or create cart for user
- `GET /api/v1/carts/{cartId}` - Get cart by ID
- `POST /api/v1/carts/user/{userId}/items` - Add item to cart
- `PUT /api/v1/carts/user/{userId}/items/{itemId}` - Update cart item
- `DELETE /api/v1/carts/user/{userId}/items/{itemId}` - Remove item from cart
- `POST /api/v1/carts/user/{userId}/clear` - Clear cart
- `POST /api/v1/carts/{cartId}/deactivate` - Deactivate cart
- `POST /api/v1/carts/process-expired` - Process expired carts
- `GET /api/v1/carts/user/{userId}/count` - Get active cart count

## Event Publishing

The service publishes the following events to RabbitMQ:
- Cart Updated
- Cart Cleared
- Cart Item Added
- Cart Item Removed

## Caching Strategy

Redis is used for caching with the following patterns:
- Active cart by user ID
- Cart by cart ID
- Cache invalidation on updates
- Configurable TTL

## Configuration

Key configuration properties in `application.properties`:
```properties
# Server
server.port=8006

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_carts

# Redis
spring.redis.host=localhost
spring.redis.port=6379
spring.cache.redis.time-to-live=15

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Application
app.cart.expiry-hours=72
app.cart.cleanup-interval=3600000
```

## Getting Started

1. Ensure MySQL, Redis, and RabbitMQ are running
2. Create database: `ecommerce_carts`
3. Update `application.properties` with your configurations
4. Run the application: `./mvnw spring-boot:run`
5. Access Swagger UI: `http://localhost:8006/docs/swagger-ui.html`

## Error Handling

The service handles various error scenarios:
- Entity not found
- Invalid request parameters
- Database constraints
- Cache failures
- Message queue issues

## Monitoring

The service exposes the following actuator endpoints:
- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus` 