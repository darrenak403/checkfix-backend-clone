package com.datnguyen.apigateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



@Component("swaggerRewrite")
public class SwaggerConfig implements RewriteFunction<String, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> apply(ServerWebExchange exchange, String body) {
        String path = exchange.getRequest().getPath().toString();
        try {
            JsonNode root = objectMapper.readTree(body);
            if (path.contains("/iam/")) {
                ((ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api/iam");
            } else if (path.contains("/patient/")) {
                ((ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api");
            }else if (path.contains("/testOrder/")) {
                ((ObjectNode) root).putArray("servers")
                        .addObject().put("url", "http://localhost:6789/v1/api");
            }
            return Mono.just(objectMapper.writeValueAsString(root));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(body);
        }
    }
}
