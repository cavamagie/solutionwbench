/**
 * SecurityConfig
 * 
 * Spring Security configuration for OAuth2 Resource Server with Keycloak.
 * Configures JWT token validation and endpoint authorization rules.
 * 
 * Security Features:
 * - OAuth2 Resource Server with JWT token validation
 * - Keycloak as the authorization server
 * - Public access to Swagger UI and API documentation
 * - Public access to actuator health endpoints
 * - Secured access to all /api/** endpoints requiring authentication
 */
package k5.giftcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class that sets up OAuth2 resource server
 * with JWT token validation against Keycloak.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    /**
     * Configures the security filter chain with OAuth2 resource server
     * and authorization rules for different endpoints.
     * 
     * Authorization Rules:
     * - Swagger UI and API docs: Public access (no authentication required)
     * - Actuator health endpoints: Public access
     * - All /api/** endpoints: Requires authentication
     * 
     * @param http HttpSecurity configuration object
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configure authorization rules
            .authorizeHttpRequests(authorize -> authorize
                // Allow public access to Swagger UI and API documentation
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()
                
                // Allow public access to actuator health endpoints
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()
                
                // Require authentication for all /api/** endpoints
                .requestMatchers("/api/**").authenticated()
                
                // Deny all other requests by default
                .anyRequest().denyAll()
            )
            
            // Configure OAuth2 Resource Server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {
                    // JWT decoder will be auto-configured from application.yaml
                    // using spring.security.oauth2.resourceserver.jwt.issuer-uri
                })
            )
            
            // Disable CSRF for stateless REST API
            .csrf(csrf -> csrf.disable())
            
            // Configure stateless session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}

// Made with Bob