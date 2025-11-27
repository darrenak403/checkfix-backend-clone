package ttldd.instrumentservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.instrumentservice.repository.RawHL7TestResultRepo;
import ttldd.instrumentservice.service.imp.RawHl7Service;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RawHl7ServiceTest {

    @Mock
    private RawHL7TestResultRepo rawHL7TestResultRepo;

    @InjectMocks
    private RawHl7Service rawHl7Service;

    @Test
    void autoDelete_RemovesOldRecords() {
        when(rawHL7TestResultRepo.deleteByCreatedAtBefore(any()))
                .thenReturn(15L);

        rawHl7Service.autoDelete();

        verify(rawHL7TestResultRepo).deleteByCreatedAtBefore(argThat(time ->
                time.isBefore(LocalDateTime.now().minusMinutes(1).plusSeconds(5))));
    }
}