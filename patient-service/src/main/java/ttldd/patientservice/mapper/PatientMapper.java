package ttldd.patientservice.mapper;

import ttldd.patientservice.dto.request.PatientRequest;
import ttldd.patientservice.dto.response.PatientResponse;
import ttldd.patientservice.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toPatientEntity(PatientRequest patientRequest);
    PatientResponse toPatientResponse(Patient patient);
}
