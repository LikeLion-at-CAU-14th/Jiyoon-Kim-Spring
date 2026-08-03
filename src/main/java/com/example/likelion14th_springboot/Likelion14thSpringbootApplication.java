package com.example.likelion14th_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Likelion14thSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(Likelion14thSpringbootApplication.class, args);
	}

}
