package edu.demo.clientesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ClientesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientesServiceApplication.class, args);
	}

}
