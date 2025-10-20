package ttldd.labman.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DateUtils {
    private static final DateTimeFormatter VN_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LocalDate parseVnDate(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("-")) {
            return null;
        }

        try {
            return LocalDate.parse(dateString, VN_DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Không thể parse ngày (format không đúng): {}", dateString);
            return null;
        }
    }

}
