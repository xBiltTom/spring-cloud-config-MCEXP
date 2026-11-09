package edu.demo.pedidosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PedidosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosServiceApplication.class, args);
	}

}
