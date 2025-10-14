package jungle.warehouseservice.repository.httpClient;

import jungle.warehouseservice.config.AuthenticationRequestInterceptor;
import jungle.warehouseservice.dto.response.RestResponse;
import jungle.warehouseservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", path = "/iam",  configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse> getUser(@PathVariable("id") Long id);
}
