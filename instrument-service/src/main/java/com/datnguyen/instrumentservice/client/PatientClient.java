package com.datnguyen.instrumentservice.client;

import com.datnguyen.instrumentservice.config.AuthenticationRequestInterceptor;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service" ,configuration = AuthenticationRequestInterceptor.class)
public interface PatientClient {
    @GetMapping("/patient/{id}")
    RestResponse<PatientResponse> getPatient(@PathVariable Long id);
}
