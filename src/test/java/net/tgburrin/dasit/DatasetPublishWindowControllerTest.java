package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import net.tgburrin.dasit.Dataset.DatasetPublishWindowController;

@AutoConfigureMockMvc
public class DatasetPublishWindowControllerTest extends BaseIntegrationTest {
	@Autowired
	private DatasetPublishWindowController dpwc;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testControllerContext() {
		assertThat(dpwc).isNotNull();
	}

}
