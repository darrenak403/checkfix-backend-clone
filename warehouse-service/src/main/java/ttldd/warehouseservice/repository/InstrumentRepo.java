package ttldd.warehouseservice.repository;

import ttldd.warehouseservice.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepo extends JpaRepository<Instrument, Long> {
}
