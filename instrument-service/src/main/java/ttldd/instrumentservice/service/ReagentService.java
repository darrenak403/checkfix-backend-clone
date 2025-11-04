package ttldd.instrumentservice.service;

import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.response.ReagentGetAllResponse;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import org.springframework.stereotype.Service;

import java.util.List;

//hello
@Service
public interface ReagentService {
    ReagentInstallResponse installReagent(ReagentInstallRequest reagentInstallRequest);
    List<ReagentGetAllResponse> getALlReagents();
}
