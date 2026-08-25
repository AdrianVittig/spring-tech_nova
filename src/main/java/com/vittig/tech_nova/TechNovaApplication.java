package com.vittig.tech_nova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechNovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechNovaApplication.class, args);
	}

}
