package ttldd.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttldd.warehouseservice.entity.Instrument;

import java.util.Optional;

public interface InstrumentRepo extends JpaRepository<Instrument, Long> {
    Optional<Instrument> findBySerialNumber(String serialNumber);
}
