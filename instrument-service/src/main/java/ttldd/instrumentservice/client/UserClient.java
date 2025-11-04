package ttldd.instrumentservice.client;


import ttldd.instrumentservice.config.AuthenticationRequestInterceptor;
import ttldd.instrumentservice.dto.response.RestResponse;
import ttldd.instrumentservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//hello
@FeignClient(name = "iam-service",path = "/iam",configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse>  getUser(@PathVariable("id") Long id);
}
