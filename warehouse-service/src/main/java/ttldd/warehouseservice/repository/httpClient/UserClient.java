package ttldd.warehouseservice.repository.httpClient;

import ttldd.warehouseservice.config.AuthenticationRequestInterceptor;
import ttldd.warehouseservice.dto.response.RestResponse;
import ttldd.warehouseservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", path = "/iam",  configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse> getUser(@PathVariable("id") Long id);
}
