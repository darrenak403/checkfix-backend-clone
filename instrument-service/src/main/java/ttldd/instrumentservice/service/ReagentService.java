package ttldd.instrumentservice.service;

import ttldd.instrumentservice.dto.request.ReagentInstallRequest;
import ttldd.instrumentservice.dto.request.UpdateReagentStatusRequest;
import ttldd.instrumentservice.dto.response.ReagentDeleteResponse;
import ttldd.instrumentservice.dto.response.ReagentGetAllResponse;
import ttldd.instrumentservice.dto.response.ReagentInstallResponse;
import org.springframework.stereotype.Service;
import ttldd.instrumentservice.dto.response.UpdateReagentStatusResponse;

import java.util.List;

//hello
@Service
public interface ReagentService {
    ReagentInstallResponse installReagent(ReagentInstallRequest reagentInstallRequest);
    List<ReagentGetAllResponse> getALlReagents();

    UpdateReagentStatusResponse updateReagentStatus(UpdateReagentStatusRequest updateReagentRequest , String reagentId );

    ReagentInstallResponse updateReagentInfo(ReagentInstallRequest reagentInstallRequest , String reagentId );

    ReagentDeleteResponse deleteReagent(String reagentId );
}
