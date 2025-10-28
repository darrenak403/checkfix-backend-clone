package ttldd.apigateway.service;

import ttldd.apigateway.dto.ApiLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoggingFilter implements GlobalFilter, Ordered {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        var request = exchange.getRequest();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - start;
            int status = exchange.getResponse().getStatusCode() != null ?
                    exchange.getResponse().getStatusCode().value() : 0;

            ApiLogDTO log = ApiLogDTO.builder()
                    .serviceName("API-Gateway")
                    .method(String.valueOf(request.getMethod()))
                    .path(request.getURI().getPath())
                    .status(status)
                    .latency(duration)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("api-logs", log);
            System.out.println("📤 [Gateway] Logged: " + log);
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}