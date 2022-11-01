package com.ccat.catbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class CatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatbotApplication.class, args);
	}

}
