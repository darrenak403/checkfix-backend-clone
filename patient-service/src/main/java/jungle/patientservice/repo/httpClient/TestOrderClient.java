package jungle.patientservice.repo.httpClient;

import jungle.patientservice.config.AuthenticationRequestInterceptor;
import jungle.patientservice.dto.response.RestResponse;
import jungle.patientservice.dto.response.TestOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "testorder-service", configuration = AuthenticationRequestInterceptor.class)
public interface TestOrderClient {
    @GetMapping("/orders/patient/{patientId}")
    RestResponse<List<TestOrderResponse>> getOrdersByPatientId(@PathVariable Long patientId);
}
