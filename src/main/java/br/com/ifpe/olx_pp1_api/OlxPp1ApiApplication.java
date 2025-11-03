package br.com.ifpe.olx_pp1_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OlxPp1ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlxPp1ApiApplication.class, args);
	}

}
