# URL Shortener Microservice

A high-performance URL shortener microservice built with Java 17 and Spring Boot 3.2, designed to handle 10k+ TPS with advanced caching, database optimization, and comprehensive exception handling.

## Features

- **High Performance**: Capable of handling 10,000+ transactions per second
- **SOLID Principles**: Well-structured, maintainable code following SOLID design principles
- **Comprehensive Exception Handling**: Robust error handling with detailed error responses
- **Caching**: Redis-based caching for optimal performance
- **Database Optimization**: Optimized database queries with proper indexing
- **Rate Limiting**: Built-in rate limiting capabilities
- **Analytics**: Click tracking and analytics
- **Custom Short Codes**: Support for custom short codes
- **URL Expiration**: Optional URL expiration functionality
- **Health Checks**: Built-in health monitoring endpoints
- **Comprehensive Testing**: Unit, integration, and performance tests

## Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA**
- **Redis** (for caching)
- **H2 Database** (development)
- **PostgreSQL** (production)
- **Maven** (build tool)
- **JUnit 5** (testing)
- **Mockito** (mocking)

## API Endpoints

### Shorten URL
```http
POST /api/v1/shorten
Content-Type: application/json

{
  "url": "https://example.com",
  "customShortCode": "custom123", // optional
  "expiresAt": "2024-12-31T23:59:59", // optional
  "description": "Example URL" // optional
}
```

**Response:**
```json
{
  "originalUrl": "https://example.com",
  "shortUrl": "http://localhost:8080/api/v1/abc123",
  "shortCode": "abc123",
  "expiresAt": "2024-12-31T23:59:59",
  "createdAt": "2024-01-01T12:00:00",
  "description": "Example URL"
}
```

### Redirect to Original URL
```http
GET /api/v1/{shortCode}
```

**Response:** HTTP 302 redirect to original URL

### Get Analytics
```http
GET /api/v1/analytics/{shortCode}
```

**Response:**
```json
{
  "originalUrl": "https://example.com",
  "shortUrl": "http://localhost:8080/api/v1/abc123",
  "shortCode": "abc123",
  "clickCount": 42,
  "createdAt": "2024-01-01T12:00:00",
  "expiresAt": "2024-12-31T23:59:59",
  "isActive": true,
  "description": "Example URL"
}
```

### Delete URL
```http
DELETE /api/v1/{shortCode}
```

**Response:** HTTP 204 No Content

### Health Check
```http
GET /api/v1/health
```

**Response:** "URL Shortener Service is running"

## Quick Start

### Prerequisites
- Java 17 or later
- Maven 3.6+
- Redis server (for caching)

### Running the Application

1. **Clone the repository:**
```bash
git clone <repository-url>
cd url-shortener
```

2. **Build the project:**
```bash
mvn clean compile
```

3. **Run tests:**
```bash
mvn test
```

4. **Start Redis server:**
```bash
redis-server
```

5. **Run the application:**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api/v1`

## Configuration

### Application Properties
The application can be configured via `application.yml`:

```yaml
urlshortener:
  base-url: http://localhost:8080/api/v1
  short-code:
    length: 6
    characters: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
  cache:
    expiration-hours: 24
  rate-limit:
    requests-per-minute: 100
    requests-per-hour: 1000
```

### Database Configuration
- **Development**: H2 in-memory database
- **Production**: PostgreSQL with connection pooling

### Redis Configuration
- **Host**: localhost
- **Port**: 6379
- **Connection Pool**: Configured for high throughput

## Performance Optimizations

### Database Optimizations
- **Connection Pooling**: HikariCP with optimized settings
- **Indexing**: Strategic indexes on frequently queried columns
- **Query Optimization**: Efficient JPA queries with proper caching

### Caching Strategy
- **Redis Cache**: TTL-based caching for frequently accessed URLs
- **Multi-level Caching**: Application-level and Redis caching
- **Cache Invalidation**: Proper cache invalidation on URL updates/deletions

### Application Optimizations
- **Async Processing**: Non-blocking operations where possible
- **Thread Pool**: Optimized thread pool configuration
- **JVM Tuning**: Recommended JVM settings for high throughput

## Error Handling

The application provides comprehensive error handling with structured error responses:

```json
{
  "errorCode": "URL_NOT_FOUND",
  "message": "URL with short code 'abc123' not found",
  "path": "/api/v1/abc123",
  "timestamp": "2024-01-01T12:00:00",
  "validationErrors": {}
}
```

### Error Codes
- `URL_NOT_FOUND` (404): Short code not found
- `URL_EXPIRED` (410): URL has expired
- `SHORT_CODE_EXISTS` (409): Custom short code already exists
- `INVALID_URL` (400): Invalid URL format
- `RATE_LIMIT_EXCEEDED` (429): Rate limit exceeded
- `VALIDATION_ERROR` (400): Request validation failed

## Testing

### Running Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn integration-test

# All tests with coverage
mvn clean test jacoco:report
```

### Test Coverage
The project includes comprehensive test coverage:
- Unit tests for all service layers
- Integration tests for API endpoints
- Performance tests for high-load scenarios

## Monitoring and Observability

### Health Checks
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics

### Logging
- Structured logging with correlation IDs
- Configurable log levels
- Performance monitoring logs

## Production Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/url-shortener-microservice-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/urlshortener
SPRING_DATASOURCE_USERNAME=urlshortener
SPRING_DATASOURCE_PASSWORD=password
SPRING_REDIS_HOST=redis-server
SPRING_REDIS_PORT=6379
```

## Security Considerations

- Input validation and sanitization
- Rate limiting to prevent abuse
- CORS configuration
- SQL injection prevention
- XSS protection headers

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For support and questions, please open an issue in the GitHub repository.