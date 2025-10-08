package jungle.warehouseservice.repository;

import jungle.warehouseservice.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentRepo extends JpaRepository<Instrument, Long> {
}
