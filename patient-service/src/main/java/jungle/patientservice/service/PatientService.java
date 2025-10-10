package jungle.patientservice.service;


import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.request.PatientUpdateRequest;
import jungle.patientservice.dto.response.PageResponse;
import jungle.patientservice.dto.response.PatientResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PatientService {
     PatientResponse createPatient(PatientRequest patientDTO);
     List<PatientResponse> getAllPatients();
     PatientResponse updatePatient(Long id, PatientUpdateRequest patientDTO);
     void deletePatient(Long id);
     PatientResponse getPatient(Long id);
     List<PatientResponse> getCurrentPatient();
     PageResponse<PatientResponse> getPatients(int page, int size);
}
