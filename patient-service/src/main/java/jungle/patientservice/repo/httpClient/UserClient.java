package jungle.patientservice.repo.httpClient;

import jungle.patientservice.config.AuthenticationRequestInterceptor;
import jungle.patientservice.dto.response.RestResponse;
import jungle.patientservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", url = "${app.user-service.url}", configuration = AuthenticationRequestInterceptor.class)
public interface UserClient {
    @GetMapping("/users/{id}")
    RestResponse<UserResponse> getUser(@PathVariable("id") Long id);
}
