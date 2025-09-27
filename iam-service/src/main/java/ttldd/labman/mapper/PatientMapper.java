package ttldd.labman.mapper;

import org.mapstruct.Mapper;
import ttldd.labman.dto.request.PatientRequest;
import ttldd.labman.dto.response.PatientResponse;
import ttldd.labman.entity.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toPatientEntity(PatientRequest patientRequest);
    PatientResponse toPatientResponse(Patient patient);
}
