package com.codeday.productivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProductivityApplication {

	@GetMapping
	public String helloWorld() {
		return "Hello World!";
	}

	@GetMapping("/message")
	public String message() {
		return "Message from Spring Boot";
	}

	public static void main(String[] args) {
		SpringApplication.run(ProductivityApplication.class, args);
	}

}
