package ttldd.instrumentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

//hello
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
@EnableFeignClients
@EnableScheduling
public class InstrumentServiceApplication {

    @Autowired
    public static void main(String[] args) {
        SpringApplication.run(InstrumentServiceApplication.class, args);
    }
}
