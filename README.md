# Movie World Service (Running)

Movie World Service is a RESTful API service that provides CRUD operations for movies. It communicates with the Movie Review service to fetch reviews for movies and serves as an API gateway between the frontend and the review service. The service is fully observable with metrics, logs, and traces using OpenTelemetry and Micrometer.

## Recent Updates

### Observability Improvements
- Fixed 404 error for `/actuator/loggers` endpoint by explicitly enabling it
- Enhanced test configuration for OpenTelemetry to avoid external dependencies
- Updated Kubernetes ConfigMaps to include loggers endpoint configuration
- Added WebConfig to serve static images from the `/images` directory

### Kubernetes Deployment Enhancements
- Increased readiness probe initialDelaySeconds from 30 to 60 seconds
- Added failureThreshold: 5 to readiness probes for more retry attempts
- Replaced RDS database with in-cluster MySQL database with persistent storage
- Added MySQL deployment with PV and PVC for data persistence

### Communication Flow Improvements
- Enhanced CORS configuration to allow requests from the Angular frontend
- Added proper static resource handling for movie images
- Fixed data duplication issue in SQL initialization

## Technologies Used

- Java 17
- Spring Boot 3.1.0
- Spring Data JPA
- MySQL
- Swagger/OpenAPI
- Spring Boot Actuator
- Prometheus for metrics
- OpenTelemetry for distributed tracing
- Micrometer for metrics and tracing
- JaCoCo for code coverage
- Docker

## Architecture

The application follows a layered architecture with clear separation of concerns:

1. **Controller Layer**: Handles HTTP requests and responses
   - `MovieController`: Provides REST endpoints for movie operations
   - `HealthCheckController`: Provides health check endpoint

2. **Service Layer**: Contains business logic
   - `MovieService`: Interface defining movie operations
   - `MovieServiceImpl`: Implementation of the MovieService interface

3. **Repository Layer**: Handles data access
   - `MovieRepository`: JPA repository for movie entities

4. **DTO Layer**: Data Transfer Objects for API communication
   - `MovieDTO`: DTO for movie data

5. **Mapper Layer**: Converts between entities and DTOs
   - `MovieMapper`: Maps between Movie entities and MovieDTOs

## Communication Flow

The MovieWorld service plays a central role in the application architecture:

1. **Frontend to MovieWorld**: 
   - The Angular frontend communicates directly with MovieWorld for movie data
   - Requests include: listing movies, getting movie details, creating/updating/deleting movies
   - Static movie images are served from MovieWorld's `/images` endpoint

2. **MovieWorld to MovieReview**:
   - When detailed movie information with reviews is requested, MovieWorld acts as an API gateway
   - MovieWorld calls the MovieReview service to fetch review data
   - MovieWorld combines movie data with review data and returns the complete response

3. **Data Flow Diagram**:
   ```
   Angular Frontend <--> MovieWorld Service <--> MovieReview Service
                          |                        |
                          v                        v
                      Movie Database          Review Database
   ```

4. **API Gateway Pattern**:
   - MovieWorld implements the API Gateway pattern for review operations
   - This simplifies the frontend by providing a single point of contact
   - Enables better security, monitoring, and error handling

6. **Model Layer**: Domain entities
   - `Movie`: Entity representing a movie

7. **Configuration Layer**: Application configuration
   - `OpenApiConfig`: Configuration for Swagger/OpenAPI
   - `ObservabilityConfig`: Configuration for metrics, tracing, and logging
   - `WebConfig`: Configuration for CORS and static resource handling

## Best Practices Implemented

- **DTO Pattern**: Separates internal data model from external API representation
- **Service Interface/Implementation**: Provides clear contracts and enables easier testing
- **Mapper Classes**: Centralizes entity-to-DTO conversion logic
- **Environment Variables**: Uses environment variables for configuration
- **Comprehensive Logging**: Logs all operations with appropriate log levels
- **API Documentation**: Uses Swagger/OpenAPI for API documentation
- **Health Checks**: Provides health check endpoint
- **Metrics**: Exposes metrics for monitoring via Prometheus and Micrometer
- **Distributed Tracing**: Uses OpenTelemetry for end-to-end tracing
- **Structured Logging**: Includes trace and span IDs in logs for correlation
- **Observability**: Full integration with Prometheus, Grafana, and Istio
- **Unit Testing**: Comprehensive unit tests for all layers
- **Code Coverage**: JaCoCo for code coverage reporting

## Prerequisites

- Java 17
- Maven
- MySQL
- Docker (optional)

## Environment Variables

The application uses the following environment variables:

- `MYSQL_URL`: MySQL database URL (default: `jdbc:mysql://localhost:3306/movieworld`)
- `MYSQL_USERNAME`: MySQL username (default: `root`)
- `MYSQL_PASSWORD`: MySQL password (default: `root`)
- `LOG_FILE_PATH`: Path to log file (default: `logs/movieworld.log`)
- `MOVIE_REVIEW_SERVICE_URL`: URL of the Movie Review service (default: `http://localhost:9093`)
- `OTEL_SDK_DISABLED`: Disable OpenTelemetry SDK (default: `false`)
- `DB_HOST`: Database host for Kubernetes deployment (default: `movie-app-db.cvggya6kg1r7.us-east-1.rds.amazonaws.com`)
- `OTEL_EXPORTER_OTLP_ENDPOINT`: OpenTelemetry collector endpoint (default: `http://localhost:4317`)

## Building the Application

```bash
mvn clean package
```

## Running the Application

### Using Maven

```bash
# Run with OpenTelemetry enabled (default)
mvn spring-boot:run

# Run with OpenTelemetry disabled
$env:OTEL_SDK_DISABLED="true"; mvn spring-boot:run
```

### Using Java

```bash
# Run with OpenTelemetry enabled (default)
java -jar target/movieworld-0.0.1-SNAPSHOT.jar

# Run with OpenTelemetry disabled
java -jar -Dotel.sdk.disabled=true target/movieworld-0.0.1-SNAPSHOT.jar
```

### Using Docker

Build the Docker image:

```bash
docker build -t com.abc/movieworld:latest .
```

Run the Docker container:

```bash
docker run -p 9091:9091 \
  -e MYSQL_URL=jdbc:mysql://mysql-host:3306/movieworld \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=root \
  -e MOVIE_REVIEW_SERVICE_URL=http://moviereview-service:9091 \
  -e OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317 \
  com.abc/movieworld:latest
```

## API Documentation

The API documentation is available at:

- Swagger UI: `http://localhost:9091/swagger-ui.html`
- OpenAPI JSON: `http://localhost:9091/api-docs`

## API Endpoints

All endpoints are available at `http://localhost:9091`:

- `GET /api/movieworld`: Get all movies
- `GET /api/movieworld/{id}`: Get a movie by ID
- `GET /api/movieworld/{id}/with-reviews`: Get a movie by ID with reviews
- `POST /api/movieworld`: Create a new movie
- `PUT /api/movieworld/{id}`: Update a movie
- `DELETE /api/movieworld/{id}`: Delete a movie
- `GET /api/movieworld/health`: Health check endpoint

## Observability

### Monitoring Endpoints

The application exposes the following endpoints for monitoring at `http://localhost:9091`:

- `GET /actuator/health`: Health check - http://localhost:9091/actuator/health
- `GET /actuator/info`: Application information - http://localhost:9091/actuator/info
- `GET /actuator/metrics`: Application metrics - http://localhost:9091/actuator/metrics
- `GET /actuator/prometheus`: Prometheus metrics - http://localhost:9091/actuator/prometheus
- `GET /actuator/loggers`: Logger configuration - http://localhost:9091/actuator/loggers

### Metrics

The application emits the following types of metrics:

- JVM metrics (memory, garbage collection, etc.)
- HTTP request metrics with histogram distributions
- Custom business metrics using `@Observed` annotations
- Service method execution metrics via `MetricsAspect`
- System metrics (CPU, memory, etc.)

#### Custom Metrics Implementation

The application implements custom metrics in two ways:

1. **@Observed Annotations**: Applied to controller methods to create both metrics and traces:

```java
@GetMapping
@Observed(name = "movie.getAll", 
         contextualName = "get-all-movies", 
         lowCardinalityKeyValues = {"service", "movie-world"})
public ResponseEntity<List<MovieDTO>> getAllMovies() {
    // Method implementation
}
```

2. **MetricsAspect**: Automatically measures and records execution time for all service methods:

```java
@Around("execution(* com.abc.movieworld.service.*.*(..))")
public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    // Records method invocation count
    // Records method execution time
    // Records error counts with exception types
}
```

#### Available Metrics

The following custom metrics are available:

- `service.[ServiceName].[methodName].invocations` - Count of method invocations
  - Example: http://localhost:9091/actuator/metrics/service.MovieServiceImpl.getMovieById.invocations
- `service.[ServiceName].[methodName].duration` - Method execution time
  - Example: http://localhost:9091/actuator/metrics/service.MovieServiceImpl.getMovieById.duration
- `service.[ServiceName].[methodName].errors` - Count of errors by exception type
- `http.server.requests` - HTTP request metrics with response time histograms
  - Example: http://localhost:9091/actuator/metrics/http.server.requests

These metrics can be viewed at `/actuator/metrics` or scraped by Prometheus from `/actuator/prometheus`.

### Distributed Tracing

The application uses OpenTelemetry for distributed tracing:

- Automatic instrumentation of HTTP requests and responses
- Trace context propagation between services
- Custom spans using `@Observed` annotations on controller methods
- Custom metrics and timing using the `MetricsAspect` for service methods
- Integration with Istio service mesh

#### OpenTelemetry Configuration

The application is configured to use OpenTelemetry for tracing with the following components:

- **OpenTelemetry SDK**: Provides the core functionality for tracing
- **OTLP Exporter**: Exports traces to an OpenTelemetry collector
- **Micrometer Bridge**: Integrates with Spring Boot's metrics system
- **Resource Attributes**: Identifies the service in traces and metrics
- **Resilient Configuration**: Gracefully handles collector unavailability

The configuration is in `ObservabilityConfig.java` and includes:

```java
@Bean
public Resource otelResource() {
    return Resource.getDefault()
        .merge(Resource.create(Attributes.of(
            ResourceAttributes.SERVICE_NAME, serviceName,
            ResourceAttributes.SERVICE_VERSION, "1.0.0"
        )));
}

@Bean
@Primary
public OpenTelemetry openTelemetry(Resource resource) {
    try {
        // Create a minimal but functional OpenTelemetry SDK
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .setResource(resource)
            .setSampler(Sampler.alwaysOn())
            .build();
        
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .build();
        
        return sdk;
    } catch (Exception e) {
        // Return the no-op implementation which will not affect application functionality
        return OpenTelemetry.noop();
    }
}
```

#### Using OpenTelemetry in Development

To run the application with OpenTelemetry tracing:

1. Start an OpenTelemetry collector:

```bash
docker run -p 4317:4317 -p 4318:4318 -p 55680:55680 otel/opentelemetry-collector-contrib:latest
```

2. Configure the application to use the collector:

```bash
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
mvn spring-boot:run
```

3. View traces in a compatible backend like Jaeger:

```bash
docker run -p 16686:16686 jaegertracing/all-in-one:latest
```

Then access the Jaeger UI at http://localhost:16686

### Logging

The application uses structured logging with trace correlation:

- Log format includes trace and span IDs
- Log levels configurable via `/actuator/loggers` endpoint
- Dynamic log level adjustment at runtime
- Logs can be correlated with traces and metrics

### Integration with Observability Stack

- **Prometheus**: Scrapes metrics from `/actuator/prometheus`
- **Grafana**: Visualizes metrics from Prometheus
- **Istio**: Collects and visualizes service mesh telemetry
- **OpenTelemetry Collector**: Receives and processes traces

## Testing

The application includes comprehensive unit tests for all layers:

### Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=MovieControllerTest

# Run a specific test method
mvn test -Dtest=MovieControllerTest#testGetMovieById

# Run tests with coverage report
mvn clean test jacoco:report
```

### Test Configuration

Tests are configured to use:

- H2 in-memory database instead of MySQL
- Disabled OpenTelemetry to avoid connection attempts to collectors during tests
- Custom test profile activated via `@ActiveProfiles("test")`

The test configuration is defined in:

1. **TestConfig.java**: Disables OpenTelemetry auto-configuration during tests

```java
@Configuration
@Profile("test")
@EnableAutoConfiguration(exclude = {
    OtlpAutoConfiguration.class,
    OtlpLoggerAutoConfiguration.class,
    OtlpMetricAutoConfiguration.class
})
public class TestConfig {
    // Test-specific beans can be defined here
}
```

2. **application.properties** (test): Configures the test environment

```properties
# Activate test profile
spring.profiles.active=test

# Configure H2 database for tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Disable OpenTelemetry for tests
management.tracing.enabled=false
spring.sql.init.mode=never
```

### Test Coverage

The project uses JaCoCo for code coverage analysis with a minimum coverage threshold of 80%. Current coverage is 81%, ensuring high-quality code.

```bash
# Run tests with coverage report
mvn clean test jacoco:report
mvn clean verify

# Run tests with coverage and skip integration tests
mvn clean verify -DskipITs
```

The latest test run shows all tests passing with 81% code coverage, meeting our quality requirements.

### Test Structure

- **Controller Tests**: Test the REST endpoints using MockMvc
- **Service Tests**: Test the business logic with mocked repositories
- **Repository Tests**: Test the data access layer using H2 in-memory database
- **Mapper Tests**: Test the entity-to-DTO conversion
- **Model Tests**: Test the entity classes
- **DTO Tests**: Test the DTO classes

### Code Coverage Report

The code coverage report will be generated at:
```
target/site/jacoco/index.html
```

To view the report, open the HTML file in a web browser after running:
```bash
mvn clean verify
```

### Coverage Thresholds

The project is configured with the following coverage thresholds:
- **Line Coverage**: 80%
- **Branch Coverage**: 80%
- **Method Coverage**: 80%
- **Class Coverage**: 80%

## Kubernetes Deployment

The application can be deployed to a Kubernetes cluster using the provided manifests or Helm chart. All components of the Movie application (MovieWorld, MovieReview, and MovieApp Frontend) are deployed in the `movie` namespace for better isolation and management.

### Prerequisites

- Kubernetes cluster (EKS or any other Kubernetes distribution)
- Istio service mesh installed
- Prometheus and Grafana for monitoring
- kubectl CLI
- Helm CLI (for Helm deployment)

### Kubernetes Manifests

The Kubernetes manifests are located in the `kubernetes/manifests` directory. All manifests are configured to use the `movie` namespace:

```
kubernetes/manifests/
├── namespace.yaml         # Movie namespace definition
├── configmap.yaml         # Application configuration
├── deployment.yaml        # Deployment configuration
├── istio-destinationrule.yaml  # Istio destination rules
├── istio-gateway.yaml     # Istio gateway configuration
├── istio-virtualservice.yaml   # Istio virtual service routes
├── secret.yaml            # Database credentials
├── service.yaml           # Service definition
├── mysql-deployment.yaml  # MySQL database deployment
├── mysql-pv-pvc.yaml      # MySQL persistent volume and claim
├── mysql-secret.yaml      # MySQL credentials
└── mysql-service.yaml     # MySQL service definition
```

#### Important Kubernetes Configuration Notes

- **Readiness Probe**: Configured with 60-second initial delay and 5 retries to ensure the application is fully initialized before receiving traffic
- **ConfigMap**: Includes explicit configuration for actuator endpoints, including loggers
- **Database**: Uses in-cluster MySQL database at `movieworld-mysql.movie.svc.cluster.local:3306`
- **Service Discovery**: Uses Kubernetes DNS to connect to MovieReview service at `http://moviereview:9093`

To deploy using the manifests:

```bash
# Create the namespace first
kubectl apply -f kubernetes/manifests/namespace.yaml

# Apply the manifests
kubectl apply -f kubernetes/manifests/

# Verify the deployment
kubectl get pods -n movie -l app=movieworld
kubectl get svc -n movie movieworld
kubectl get virtualservice -n movie movieworld
```

### Helm Chart

The Helm chart is located in the `kubernetes/helm/movieworld` directory:

```
kubernetes/helm/movieworld/
├── Chart.yaml            # Chart metadata
├── templates/            # Kubernetes manifest templates
│   ├── _helpers.tpl      # Template helpers
│   ├── configmap.yaml    # ConfigMap template
│   ├── deployment.yaml   # Deployment template
│   ├── hpa.yaml          # HorizontalPodAutoscaler template
│   ├── istio-destinationrule.yaml  # Istio DestinationRule template
│   ├── istio-gateway.yaml          # Istio Gateway template
│   ├── istio-virtualservice.yaml   # Istio VirtualService template
│   ├── secret.yaml       # Secret template
│   ├── service.yaml      # Service template
│   ├── serviceaccount.yaml         # ServiceAccount template
│   └── servicemonitor.yaml         # ServiceMonitor template for Prometheus
└── values.yaml           # Default configuration values
```

### Service Discovery in Kubernetes

When running in Kubernetes, the application uses Kubernetes service discovery for inter-service communication:

1. **Service Names as DNS**: 
   - The MovieWorld service connects to the MovieReview service using the Kubernetes service name: `http://moviereview.movie.svc.cluster.local:9093`
   - This is configured in the ConfigMap and passed as an environment variable `MOVIE_REVIEW_SERVICE_URL`
   - Kubernetes DNS automatically resolves the service name to the correct pod IP(s)
   - The frontend connects to MovieWorld using the service name: `http://movieworld.movie.svc.cluster.local:9091`
   - All services are in the `movie` namespace for better organization

2. **Benefits**:
   - No hardcoded IPs - service discovery is automatic
   - Works with pod scaling and recreation - the service name remains stable
   - Compatible with Istio service mesh for advanced traffic management

To deploy using Helm:

```bash
# Create the namespace if it doesn't exist
kubectl create namespace movie

# Install the chart in the movie namespace
helm install movieworld kubernetes/helm/movieworld --namespace movie

# Upgrade an existing release
helm upgrade movieworld kubernetes/helm/movieworld --namespace movie

# Customize the deployment
helm install movieworld kubernetes/helm/movieworld --namespace movie \
  --set replicaCount=3 \
  --set image.tag=v1.0.0 \
  --set opentelemetry.endpoint=http://custom-otel-collector:4317
```

### Observability in Kubernetes

When deployed to Kubernetes with Istio:

1. **Metrics**: 
   - Prometheus automatically scrapes metrics from `/actuator/prometheus` endpoint
   - ServiceMonitor resource configures Prometheus Operator for scraping
   - View metrics in Grafana dashboards or Istio's Kiali dashboard
   - Both application metrics and JVM metrics are collected
   - Custom metrics from `@Observed` annotations and `MetricsAspect` are included

2. **Tracing**:
   - OpenTelemetry exports traces to the configured collector
   - Istio adds its own tracing headers and spans
   - View traces in Jaeger or other tracing backends

3. **Logging**:
   - Container logs are collected by the Kubernetes logging stack
   - Structured logs include trace and span IDs for correlation

4. **Service Mesh Monitoring**:
   - Istio provides additional metrics and visualizations
   - Kiali dashboard shows service topology and health
   - Grafana dashboards for Istio metrics are available
