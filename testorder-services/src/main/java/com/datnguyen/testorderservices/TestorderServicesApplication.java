package com.datnguyen.testorderservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class TestorderServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestorderServicesApplication.class, args);
	}

}
