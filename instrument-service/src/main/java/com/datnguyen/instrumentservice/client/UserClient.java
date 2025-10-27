package com.datnguyen.instrumentservice.client;


import com.datnguyen.instrumentservice.config.AuthenticationRequestInterceptor;
import com.datnguyen.instrumentservice.dto.response.RestResponse;
import com.datnguyen.instrumentservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "iam-service", url = "${app.user-service.url}",configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse>  getUser(@PathVariable("id") Long id);
}
