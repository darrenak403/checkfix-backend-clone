package jungle.patientservice.service;


import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.response.PatientResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PatientService {
    public PatientResponse createPatient(PatientRequest patientDTO);
    public List<PatientResponse> getAllPatients();
    public PatientResponse updatePatient(Long id, PatientRequest patientDTO);
    public void deletePatient(Long id);
    public PatientResponse getPatient(Long id);
}
