package com.datnguyen.instrumentservice.client;


import com.datnguyen.instrumentservice.config.AuthenticationRequestInterceptor;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "warehouse-service",
 configuration = AuthenticationRequestInterceptor.class)

public interface WareHouseClient {
    @GetMapping("instruments/{id}")
    RestResponse<WareHouseDTO> getById(@PathVariable Long id);
}
