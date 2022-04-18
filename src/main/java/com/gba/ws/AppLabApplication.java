package com.gba.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author Kavyashree
 * @createdOn June 8, 2019 3:26:23 PM
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AppLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppLabApplication.class, args);
	}

}
