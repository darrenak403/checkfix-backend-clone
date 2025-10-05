package com.datnguyen.apigateway.service;

import com.datnguyen.apigateway.dto.request.IntrospectRequest;
import com.datnguyen.apigateway.dto.response.IntrospectResponse;
import com.datnguyen.apigateway.dto.response.RestResponse;
import com.datnguyen.apigateway.repository.IamClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IamService {

    IamClient iamClient;

    public Mono<RestResponse<IntrospectResponse>> introspectToken(String token) {
        return iamClient.introspect(IntrospectRequest.builder()
                .token(token)
                .build());
    }

}
