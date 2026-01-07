package com.abc.movieworld.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration to disable OpenTelemetry in test environment.
 * This ensures tests can run without requiring an OpenTelemetry collector.
 */
@Configuration
@Profile("test")
@EnableAutoConfiguration(excludeName = {
    "io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration",
    "org.springframework.boot.actuate.autoconfigure.tracing.otlp.OtlpAutoConfiguration"
})
public class TestConfig {
    // Configuration class to disable OpenTelemetry for tests
}
