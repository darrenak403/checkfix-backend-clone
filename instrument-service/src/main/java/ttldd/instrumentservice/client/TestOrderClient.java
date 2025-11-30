package ttldd.instrumentservice.client;

import ttldd.instrumentservice.config.AuthenticationRequestInterceptor;
import ttldd.instrumentservice.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "testOrder-service", path = "/testorder", configuration = AuthenticationRequestInterceptor.class)
public interface TestOrderClient {
    @GetMapping("/orders/accessionNumber/testOrder/{accessionNumber}")
    RestResponse<TestOrderDTO> getTestOrdersByAccessionNumber(@PathVariable String accessionNumber);
}
