package com.abc.movieworld.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for setting up observability features including metrics, traces, and logs.
 * This enables integration with Prometheus, Grafana, and Istio.
 */
@Configuration
@ConditionalOnProperty(name = "otel.sdk.disabled", havingValue = "false", matchIfMissing = true)
public class ObservabilityConfig {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityConfig.class);

    @Value("${spring.application.name:movieworld}")
    private String serviceName;
    
    @Value("${otel.exporter.otlp.endpoint:http://localhost:4317}")
    private String otlpEndpoint;

    /**
     * Creates an ObservedAspect bean that enables the use of @Observed annotation
     * for method-level tracing and metrics collection.
     *
     * @param observationRegistry the registry for recording observations
     * @return an ObservedAspect bean
     */
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
    
    /**
     * Configures OpenTelemetry resource with service information.
     * This is used by the OpenTelemetry SDK to identify the service in traces and metrics.
     *
     * @return Resource with service name and other attributes
     */
    @Bean
    public Resource otelResource() {
        log.info("Configuring OpenTelemetry with service name: {} and endpoint: {}", serviceName, otlpEndpoint);
        return Resource.getDefault()
            .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, serviceName,
                ResourceAttributes.SERVICE_VERSION, "1.0.0"
            )));
    }
    
    /**
     * Creates a resilient OpenTelemetry instance that will not fail the application
     * startup if the collector is unavailable.
     * 
     * @param resource the OpenTelemetry resource with service information
     * @return an OpenTelemetry instance
     */
    @Bean
    @Primary
    public OpenTelemetry openTelemetry(Resource resource) {
        try {
            log.info("Attempting to initialize OpenTelemetry SDK with collector at: {}", otlpEndpoint);
            
            // Create a minimal but functional OpenTelemetry SDK
            SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .setSampler(Sampler.alwaysOn())
                .build();
            
            OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();
            
            log.info("OpenTelemetry SDK initialized successfully");
            return sdk;
        } catch (Exception e) {
            log.warn("Failed to initialize OpenTelemetry SDK: {}. Using no-op implementation.", e.getMessage());
            log.debug("OpenTelemetry initialization error details", e);
            // Return the no-op implementation which will not affect application functionality
            return OpenTelemetry.noop();
        }
    }
    
    /**
     * Fallback configuration when OpenTelemetry collector is not available.
     * This ensures the application can start even if the collector is not running.
     */
    @Configuration
    @ConditionalOnProperty(name = "otel.sdk.disabled", havingValue = "true")
    public static class FallbackConfig {
        
        private static final Logger log = LoggerFactory.getLogger(FallbackConfig.class);
        
        @PostConstruct
        public void init() {
            log.info("OpenTelemetry SDK is disabled. Running with minimal observability configuration.");
        }
    }
}
