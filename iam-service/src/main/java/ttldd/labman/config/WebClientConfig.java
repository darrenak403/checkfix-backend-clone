package ttldd.labman.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${vnpt.ekyc.api.url}")
    private String apiUrl;

    @Value("${vnpt.ekyc.api.token-id}")
    private String tokenId;

    @Value("${vnpt.ekyc.api.token-key}")
    private String tokenKey;

    @Value("${vnpt.ekyc.api.access-token}")
    private String accessToken;

    @Bean
    public WebClient vnptWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl) // Đặt URL cơ sở
                .defaultHeader("Token-id", tokenId)
                .defaultHeader("Token-key", tokenKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }
}
