package com.datnguyen.instrumentservice.service;

import com.datnguyen.instrumentservice.dto.request.ReagentInstallRequest;
import com.datnguyen.instrumentservice.dto.response.ReagentGetAllResponse;
import com.datnguyen.instrumentservice.dto.response.ReagentInstallResponse;
import org.springframework.stereotype.Service;

import java.util.List;

//hello
@Service
public interface ReagentService {
    ReagentInstallResponse installReagent(ReagentInstallRequest reagentInstallRequest);
    List<ReagentGetAllResponse> getALlReagents();
}
