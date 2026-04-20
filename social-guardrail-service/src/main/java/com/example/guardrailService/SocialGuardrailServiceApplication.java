package com.example.guardrailService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SocialGuardrailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialGuardrailServiceApplication.class, args);
	}

}
