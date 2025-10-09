package com.datnguyen.testorderservices.client;

import com.datnguyen.testorderservices.config.AuthenticationRequestInterceptor;
import com.datnguyen.testorderservices.dto.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "patient-service",
        url = "${app.patient-service.url}", configuration = AuthenticationRequestInterceptor.class)

public interface PatientClient {
    @GetMapping("/patient/{id}")


    RestResponse<PatientDTO> getById(@PathVariable("id") Long id);

}