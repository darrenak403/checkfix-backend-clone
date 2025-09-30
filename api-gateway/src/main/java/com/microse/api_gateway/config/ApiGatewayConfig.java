package com.microse.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayConfig {

    // ⚡ Whitelist cho Swagger + Actuator
    private static final String[] WHITE_LIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/patient/**" ,
            "/api/iam/v3/api-docs",
            "/patient/v3/api-docs",
            "/actuator/**"
    };

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // 🔹 IAM Service - Auth routes
                .route("iam-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://BE"))

                // 🔹 IAM Service - Docs
                .route("iam-docs", r -> r.path("/api/iam/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://BE"))

                // 🔹 Patient Service routes
                .route("patient-service", r -> r.path("/patient/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PATIENT-SERVICE"))

                // 🔹 Patient Service - Docs
                .route("patient-docs", r -> r.path("/patient/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://PATIENT-SERVICE"))

                // 🔹 Health check IAM Service
                .route("iam-health", r -> r.path("/health/iam")
                        .filters(f -> f.setPath("/actuator/health"))
                        .uri("lb://BE"))

                // 🔹 Health check Patient Service
                .route("patient-health", r -> r.path("/health/patient")
                        .filters(f -> f.setPath("/actuator/health"))
                        .uri("lb://PATIENT-SERVICE"))

                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(WHITE_LIST).permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
