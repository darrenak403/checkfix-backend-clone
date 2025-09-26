package ttldd.labman.service;

import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    public PatientResponse createPatient(PatientRequest patientDTO);
    public List<PatientResponse> getAllPatients();
}
