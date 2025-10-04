package com.datnguyen.apigateway.repository;

import com.datnguyen.apigateway.dto.request.IntrospectRequest;
import com.datnguyen.apigateway.dto.response.IntrospectResponse;
import com.datnguyen.apigateway.dto.response.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IamClient {

    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<RestResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);

}
