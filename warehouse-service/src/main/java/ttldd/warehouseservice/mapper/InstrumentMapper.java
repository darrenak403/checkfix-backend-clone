package ttldd.warehouseservice.mapper;

import ttldd.warehouseservice.dto.request.InstrumentRequest;
import ttldd.warehouseservice.dto.response.InstrumentResponse;
import ttldd.warehouseservice.entity.Instrument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    Instrument toInstrumentEntity(InstrumentRequest instrumentRequest);
    InstrumentResponse toInstrumentResponse(Instrument instrument);
}
