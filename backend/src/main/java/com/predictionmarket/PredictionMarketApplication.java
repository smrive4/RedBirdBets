package com.predictionmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

/* Main application class for the Prediction Market */
@SpringBootApplication
public class PredictionMarketApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().directory("./").load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(PredictionMarketApplication.class, args);
	}
}