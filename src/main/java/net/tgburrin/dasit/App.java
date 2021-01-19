package net.tgburrin.dasit;

import org.springframework.boot.CommandLineRunner;

/*
 * Dataset Advertisement System Integration Tool
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}
    @Override
    public void run(String... args) throws Exception {
        //System.out.println("Application running...");
    }
}
