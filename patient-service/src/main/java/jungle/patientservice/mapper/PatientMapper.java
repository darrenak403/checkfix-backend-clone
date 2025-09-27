package jungle.patientservice.mapper;

import jungle.patientservice.dto.request.PatientRequest;
import jungle.patientservice.dto.response.PatientResponse;
import jungle.patientservice.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toPatientEntity(PatientRequest patientRequest);
    PatientResponse toPatientResponse(Patient patient);
}
