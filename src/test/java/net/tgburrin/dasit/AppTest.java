package net.tgburrin.dasit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:database_test.properties")
class AppTests {
	@Test
	void contextLoads() {
	}
}