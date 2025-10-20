package ttldd.patientservice.service;


import ttldd.event.dto.UserUpdatedEvent;
import ttldd.patientservice.dto.request.PatientRequest;
import ttldd.patientservice.dto.request.PatientUpdateRequest;
import ttldd.patientservice.dto.response.PageResponse;
import ttldd.patientservice.dto.response.PatientResponse;
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
     void asyncPatientFromUser(UserUpdatedEvent event);
}
