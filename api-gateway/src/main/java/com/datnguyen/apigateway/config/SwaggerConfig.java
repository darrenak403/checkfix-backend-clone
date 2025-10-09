package com.datnguyen.apigateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



@Component("swaggerRewrite")
public class SwaggerConfig implements RewriteFunction<String, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> apply(org.springframework.web.server.ServerWebExchange exchange, String body) {
        String path = exchange.getRequest().getPath().toString();
        try {
            JsonNode root = objectMapper.readTree(body);
            if (path.contains("/iam/")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api/iam");
            } else if (path.contains("/patient/")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api");
            }else if (path.contains("/testOrder/")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api");
            }
            return Mono.just(objectMapper.writeValueAsString(root));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(body);
        }
    }
}
