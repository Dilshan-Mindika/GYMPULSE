package com.nexus.GYMPULSE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Injects the allowed origins for CORS from application.properties, defaulting to localhost:5173
    @Value("${cors.allowedOrigins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the application.
     * Allows requests from the specified 'allowedOrigins' (typically the frontend URL).
     * Permits common HTTP methods and all headers, and allows credentials.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins(allowedOrigins) // Origins allowed to access
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies and authentication headers
    }

    /**
     * Creates a filter bean that adds various security-related HTTP headers to every response.
     * These headers help protect against common web vulnerabilities.
     * @return OncePerRequestFilter for adding security headers.
     */
    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                // X-Content-Type-Options
                response.setHeader("X-Content-Type-Options", "nosniff");

                // X-XSS-Protection - Deprecated but can be set for older browsers
                // Modern browsers use Content-Security-Policy
                response.setHeader("X-XSS-Protection", "1; mode=block");

                // Content-Security-Policy - Start with a restrictive policy
                // This might need to be adjusted based on application needs (e.g., loading scripts/styles from CDNs)
                response.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none';");
                // For a stricter CSP, consider: "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; font-src 'self'; connect-src 'self'; frame-ancestors 'none';"

                // X-Frame-Options
                response.setHeader("X-Frame-Options", "DENY"); // Or SAMEORIGIN if framing from the same origin is needed

                // HTTP Strict Transport Security (HSTS) - uncomment and configure if HTTPS is enforced
                // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                // Referrer-Policy
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // Permissions-Policy (Feature-Policy)
                response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");


                filterChain.doFilter(request, response);
            }
        };
    }
}

