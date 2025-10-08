package jungle.warehouseservice.mapper;

import jungle.warehouseservice.dto.request.InstrumentRequest;
import jungle.warehouseservice.dto.response.InstrumentResponse;
import jungle.warehouseservice.entity.Instrument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    Instrument toInstrumentEntity(InstrumentRequest instrumentRequest);
    InstrumentResponse toInstrumentResponse(Instrument instrument);
}
