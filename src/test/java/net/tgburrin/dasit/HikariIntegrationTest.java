package net.tgburrin.dasit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HikariIntegrationTest extends BaseIntegrationTest {
	@Autowired
	private DataSource dataSource;

	@Test
	public void hikariConnectionPoolIsConfigured() {
		System.out.println("Class name: "+dataSource.getClass().getName());
		assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
	}
}