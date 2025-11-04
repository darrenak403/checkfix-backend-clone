package ttldd.instrumentservice.client;


import ttldd.instrumentservice.config.AuthenticationRequestInterceptor;
import ttldd.instrumentservice.dto.request.InstrumentUpdateRequest;
import ttldd.instrumentservice.dto.response.InstrumentResponse;
import ttldd.instrumentservice.dto.response.RestResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
//hello
@FeignClient(name = "warehouse-service", path = "/warehouse",
 configuration = AuthenticationRequestInterceptor.class)

public interface WareHouseClient {
    @GetMapping("/instruments/{id}")
    RestResponse<InstrumentResponse> getById(@PathVariable Long id);

    @PutMapping("/instruments/{id}")
    RestResponse<InstrumentResponse> updateStatus(@PathVariable Long id,
                                                  @RequestBody InstrumentUpdateRequest instrumentStatus);

}
