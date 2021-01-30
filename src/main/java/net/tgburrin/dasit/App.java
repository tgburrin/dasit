package net.tgburrin.dasit;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.CommandLineRunner;

/*
 * Dataset Advertisement System Integration Tool
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class App implements CommandLineRunner {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//System.out.println("Application running...");
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Spring boot application running in UTC timezone :"+new Date());
	}
}
