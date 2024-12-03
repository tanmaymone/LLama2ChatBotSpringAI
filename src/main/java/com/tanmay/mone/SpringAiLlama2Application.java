package com.tanmay.mone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiLlama2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiLlama2Application.class, args);
	}
// docker compose -f docker-compose.yml up
// docker exec -it ollama run llama2
}
