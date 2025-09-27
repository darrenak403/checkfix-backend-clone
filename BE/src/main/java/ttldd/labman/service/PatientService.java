package ttldd.labman.service;

import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    public PatientResponse createPatient(PatientRequest patientDTO);
    public List<PatientResponse> getAllPatients();
    public PatientResponse updatePatient(Long id, PatientRequest patientDTO);
<<<<<<< HEAD
    public void deletePatient(Long id);
=======

>>>>>>> a46baebf7f0416d41011ef25bb7d5a36256d5562
}
