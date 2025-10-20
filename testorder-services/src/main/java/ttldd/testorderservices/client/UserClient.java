package ttldd.testorderservices.client;


import ttldd.testorderservices.config.AuthenticationRequestInterceptor;
import ttldd.testorderservices.dto.response.RestResponse;
import ttldd.testorderservices.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", url = "${app.user-service.url}", configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse> getUser(@PathVariable("id") Long id);
}
