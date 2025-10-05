package com.datnguyen.testorderservices.client;

import com.datnguyen.testorderservices.config.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${app.services.patient}", configuration = AuthenticationRequestInterceptor.class)
public interface PatientClient {
    @GetMapping("/patient/{id}")
    PatientDTO getById(@PathVariable Long id);
}