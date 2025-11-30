package ttldd.apigateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;


@Component("swaggerRewrite")
public class SwaggerConfig implements RewriteFunction<String, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Override
//    public Mono<String> apply(ServerWebExchange exchange, String body) {
//        String path = exchange.getRequest().getPath().toString();
//        try {
//            JsonNode root = objectMapper.readTree(body);
//            if (path.contains("/iam/")) {
//                ((ObjectNode) root).putArray("servers")
//                        .addObject().put("url", "http://localhost:6789/v1/api/iam");
//            } else if (path.contains("/patient/")) {
//                ((ObjectNode) root).putArray("servers")
//                        .addObject().put("url", "http://localhost:6789/v1/api");
//            }else if (path.contains("/testOrder/")) {
//                ((ObjectNode) root).putArray("servers")
//                        .addObject().put("url", "http://localhost:6789/v1/api");
//            }
//            return Mono.just(objectMapper.writeValueAsString(root));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Mono.just(body);
//        }
//    }

    private static final Map<String, String> SWAGGER_PATH_MAP = new LinkedHashMap<>();

    static {
        SWAGGER_PATH_MAP.put("/iam/", "http://localhost:6789/v1/api/iam");
        SWAGGER_PATH_MAP.put("/patient/", "http://localhost:6789/v1/api");
        SWAGGER_PATH_MAP.put("/testorder/", "http://localhost:6789/v1/api/testorder");
        SWAGGER_PATH_MAP.put("/warehouse/", "http://localhost:6789/v1/api/warehouse");
        SWAGGER_PATH_MAP.put("/instrument", "http://localhost:6789m/v1/api/instrument");
        SWAGGER_PATH_MAP.put("/monitoring", "http://localhost:6789/v1/api/monitoring");
    }

    @Override
    public Mono<String> apply(ServerWebExchange exchange, String body) {
        String path = exchange.getRequest().getPath().toString();
        try {
            JsonNode root = objectMapper.readTree(body);

            for (Map.Entry<String, String> entry : SWAGGER_PATH_MAP.entrySet()) {
                if (path.contains(entry.getKey())) {
                    ((ObjectNode) root).putArray("servers")
                            .addObject()
                            .put("url", entry.getValue());
                    break; // Dừng khi đã match 1 path
                }
            }

            return Mono.just(objectMapper.writeValueAsString(root));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just(body);
        }
    }
}
