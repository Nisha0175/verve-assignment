package com.verve.verve_assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VerveAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerveAssignmentApplication.class, args);
	}

}
