package net.tgburrin.dasit;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class AppDataSource {
	private static String getdbProperties() throws IOException {
		String dbProperties;
		if ( System.getProperty("dasit.dbconfig") != null ) {
			dbProperties = System.getProperty("dasit.dbconfig");
		} else {
			Properties p = new Properties();
			p.load(AppDataSource.class.getResourceAsStream("/application.properties"));
			dbProperties = p.getProperty("dasit.dbconfig", "/database.properties");
		}
		return dbProperties;
	}

	@Primary
	@Bean
	public static DataSource getDasitDataSource() throws IOException {
		return new HikariDataSource(new HikariConfig(getdbProperties()));
	}
}
