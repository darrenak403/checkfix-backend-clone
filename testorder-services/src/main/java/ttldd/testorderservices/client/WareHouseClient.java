package ttldd.testorderservices.client;




import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ttldd.testorderservices.config.AuthenticationRequestInterceptor;
import ttldd.testorderservices.dto.response.InstrumentResponse;
import ttldd.testorderservices.dto.response.RestResponse;

//hello
@FeignClient(name = "warehouse-service", path = "/warehouse",
 configuration = AuthenticationRequestInterceptor.class)
public interface WareHouseClient {
    @GetMapping("/instruments/{id}")
    RestResponse<InstrumentResponse> getById(@PathVariable Long id);
}
