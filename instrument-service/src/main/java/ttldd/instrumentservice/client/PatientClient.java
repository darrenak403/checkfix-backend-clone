package ttldd.instrumentservice.client;

import ttldd.instrumentservice.config.AuthenticationRequestInterceptor;
import ttldd.instrumentservice.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service" ,configuration = AuthenticationRequestInterceptor.class)
public interface PatientClient {
    @GetMapping("/patient/{id}")
    RestResponse<PatientResponse> getPatient(@PathVariable Long id);
}
