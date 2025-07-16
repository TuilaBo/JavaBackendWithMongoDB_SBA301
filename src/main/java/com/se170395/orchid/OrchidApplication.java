package com.se170395.orchid;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("repository")
@ComponentScan(basePackages = {"controller", "service", "repository", "pojo", "config","security"})
public class OrchidApplication   {

	public static void main(String[] args) {
		SpringApplication.run(OrchidApplication.class, args);
	}

}
