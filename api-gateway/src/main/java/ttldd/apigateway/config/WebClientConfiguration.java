package ttldd.apigateway.config;


import ttldd.apigateway.repository.IamClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class WebClientConfiguration {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder()
                .filter(forwardAuthHeader());
    }

    @Bean
    public ExchangeFilterFunction forwardAuthHeader() {
        return (clientRequest, next) -> Mono.deferContextual(contextView ->
                contextView.<ServerWebExchange>getOrEmpty(ServerWebExchange.class)
                        .map(serverWebExchange -> {
                            String token = serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                            if (token != null && !token.isEmpty()) {
                                ClientRequest newRequest = ClientRequest.from(clientRequest)
                                        .header(HttpHeaders.AUTHORIZATION, token)
                                        .build();
                                return next.exchange(newRequest);
                            }
                            return next.exchange(clientRequest);
                        })
                        .orElse(next.exchange(clientRequest))
        );
    }

    @Bean
    IamClient iamClient(WebClient.Builder builder) {
        WebClient webClient = builder
                .baseUrl("lb://iam-service/iam")
                .build();

        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(IamClient.class);
    }

    @Bean
    CorsWebFilter corsWebFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000" , "https://dupssapp.id.vn" , "https://admin.dupssapp.id.vn" , "https://api.dupssapp.id.vn"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }

}
