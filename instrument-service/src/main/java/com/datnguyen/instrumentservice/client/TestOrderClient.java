package com.datnguyen.instrumentservice.client;

import com.datnguyen.instrumentservice.config.AuthenticationRequestInterceptor;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "testOrder-service", url = "${app.testOrder-service.url}",configuration = AuthenticationRequestInterceptor.class)
public interface TestOrderClient {
    @GetMapping("/orders/accessionNumber/testOrder/{accessionNumber}")
    RestResponse<TestOrderDTO> getTestOrdersByAccessionNumber(@PathVariable String accessionNumber);
}
