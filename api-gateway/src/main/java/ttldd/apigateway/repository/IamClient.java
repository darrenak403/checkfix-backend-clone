package ttldd.apigateway.repository;

import ttldd.apigateway.dto.request.IntrospectRequest;
import ttldd.apigateway.dto.response.IntrospectResponse;
import ttldd.apigateway.dto.response.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IamClient {

    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<RestResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);

}
