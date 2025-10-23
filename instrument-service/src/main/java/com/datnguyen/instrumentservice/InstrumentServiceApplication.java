package com.datnguyen.instrumentservice;

import com.datnguyen.instrumentservice.service.InstrumentSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class InstrumentServiceApplication  implements CommandLineRunner {

    @Autowired
    private InstrumentSerivce instrumentSerivce;
    public static void main(String[] args) {

        SpringApplication.run(InstrumentServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        instrumentSerivce.saveInstruments();
    }

}
