package com.datnguyen.instrumentservice.client;


import com.datnguyen.instrumentservice.config.AuthenticationRequestInterceptor;
import com.datnguyen.instrumentservice.dto.request.InstrumentUpdateRequest;
import com.datnguyen.instrumentservice.dto.response.InstrumentResponse;
import com.datnguyen.instrumentservice.dto.response.RestResponse;

import com.datnguyen.instrumentservice.entity.InstrumentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "warehouse-service", path = "/warehouse",
 configuration = AuthenticationRequestInterceptor.class)

public interface WareHouseClient {
    @GetMapping("/instruments/{id}")
    RestResponse<InstrumentResponse> getById(@PathVariable Long id);

    @PutMapping("/instruments/{id}")
    RestResponse<InstrumentResponse> updateStatus(@PathVariable Long id,
                                                  @RequestBody InstrumentUpdateRequest instrumentStatus);

}
