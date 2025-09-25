package ttldd.labman.service;

import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;

public interface PatientService {
    public PatientResponse createPatient(PatientRequest patientDTO);

}
