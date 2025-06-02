# E-commerce Commons Library

This library contains common utilities and standardized components used across the e-commerce microservices project.

## Features

### API Response Handling
- Standardized `ApiResponse` class for consistent response format
- Helper methods for creating common response types (OK, Created, Error)
- Timestamp included in all responses

### Exception Handling
- Global exception handler for common exceptions
- Base exception class for custom exceptions
- Common business exceptions:
  - ResourceNotFoundException
  - BadRequestException
  - UnauthorizedException

## Usage

1. Add the dependency to your service's `pom.xml`:
```xml
<dependency>
    <groupId>org.ecommerce</groupId>
    <artifactId>ecommerce-commons</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Use the standardized response format in your controllers:
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
    UserDto user = userService.getUser(id);
    return ApiResponse.ok(user);
}
```

3. Throw custom exceptions when needed:
```java
if (user == null) {
    throw new ResourceNotFoundException("User", "id", id);
}
```

4. The global exception handler will automatically handle exceptions and return appropriate responses.

## Contributing

When adding new features to this commons library:
1. Ensure the feature is truly common across multiple services
2. Maintain backward compatibility
3. Add appropriate documentation
4. Follow the existing code style and patterns 