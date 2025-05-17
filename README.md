# GitHub Repository Scoring System

A Spring Boot application that provides a scoring system for GitHub repositories.

## Features

- RESTful API endpoint for repository scoring
- Caching support using Caffeine
- Input validation
- Spring Boot 3.2.4
- Docker support

## Technology Stack

- Java 17
- Spring Boot 3.2.4
- Spring Web
- Spring Validation
- Spring Cache
- Caffeine 
- Spring Actuator
- Lombok
- Maven
- Docker

## Prerequisites

- Java 17 or higher
- Maven 3.6.x or higher
- Docker (optional, for containerized deployment)

## Building the Project

1. Clone the repository:
```bash
git clone [repository-url]
cd github-scoring
```

2. Build the project using Maven:
```bash
mvn clean install
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using Java
```bash
java -jar target/github-scoring-0.0.1-SNAPSHOT.jar
```

### Using Docker
1. Build the Docker image:
```bash
docker build -t github-scoring .
```

2. Run the container:
```bash
docker run -p 8080:8080 github-scoring
```

The application will be available at http://localhost:8080

## Configuration

The application can be configured through `application.yml` files in the `src/main/resources` directory.

## Testing

Run the tests using Maven:
```bash
mvn test
```

## API Documentation

The API documentation is available through Swagger UI. Once the application is running, you can access the documentation at:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Specification: http://localhost:8080/v3/api-docs

## Monitoring and Health Checks

The application includes Spring Boot Actuator for monitoring and health checks. The following endpoints are available:

- Health Check: http://localhost:8080/actuator/health
- Application Info: http://localhost:8080/actuator/info
- Metrics: http://localhost:8080/actuator/metrics

## Future Improvements

- Add Redis caching for better performance with high traffic
- Add Kubernetes deployment configurations
- Implement horizontal pod autoscaling
