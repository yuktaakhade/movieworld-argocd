package com.abc.movieworld.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObservabilityConfigTest {
    
    /**
     * Test configuration to provide mock beans
     */
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public OpenTelemetry mockOpenTelemetry() {
            return OpenTelemetry.noop();
        }
    }

    @Nested
    @SpringBootTest(classes = {ObservabilityConfig.class, ObservabilityConfigTest.TestConfig.class})
    @ActiveProfiles("test")
    @TestPropertySource(properties = {"spring.application.name=test-service", "otel.sdk.disabled=false", "management.tracing.enabled=true"})
    class MainConfigTest {
        @Autowired
        private ObservabilityConfig observabilityConfig;

        @MockBean
        private ObservationRegistry observationRegistry;

        @Test
        void testObservedAspectCreation() {
            ObservedAspect aspect = observabilityConfig.observedAspect(observationRegistry);
            assertNotNull(aspect, "ObservedAspect should not be null");
        }

        @Test
        void testOtelResourceCreation() {
            Resource resource = observabilityConfig.otelResource();
            assertNotNull(resource, "Resource should not be null");
            
            // Verify that the resource has the expected attributes
            assertTrue(resource.getAttributes().asMap().containsKey(ResourceAttributes.SERVICE_NAME),
                    "Resource should contain SERVICE_NAME attribute");
            assertTrue(resource.getAttributes().asMap().containsKey(ResourceAttributes.SERVICE_VERSION),
                    "Resource should contain SERVICE_VERSION attribute");
        }
    }
    
    @Nested
    @SpringBootTest(classes = {ObservabilityConfig.FallbackConfig.class, ObservabilityConfigTest.TestConfig.class})
    @TestPropertySource(properties = {"otel.sdk.disabled=true"})
    class FallbackConfigTest {
        @Test
        void testFallbackConfig() {
            // Create an instance of FallbackConfig and call init() to test the method
            ObservabilityConfig.FallbackConfig fallbackConfig = new ObservabilityConfig.FallbackConfig();
            
            // Verify that the fallback config instance is created successfully
            assertNotNull(fallbackConfig, "FallbackConfig instance should not be null");
            
            // Test that init() method executes without throwing any exceptions
            try {
                fallbackConfig.init();
                // If we reach this point, the method executed successfully
                assertTrue(true, "FallbackConfig.init() should execute without throwing exceptions");
            } catch (Exception e) {
                // If an exception is thrown, the test should fail
                assertTrue(false, "FallbackConfig.init() should not throw any exceptions: " + e.getMessage());
            }
        }
    }
}
