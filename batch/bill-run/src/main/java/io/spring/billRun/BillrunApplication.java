package io.spring.billRun;

import io.spring.billRun.config.SpringApplicationFailedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillrunApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BillrunApplication.class);
		app.addListeners(new SpringApplicationFailedEvent());

		app.run(args);
	}
}
