package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import net.tgburrin.dasit.Dataset.DatasetRegistrationController;

@AutoConfigureMockMvc
public class DatasetReistrationControllerTest extends BaseIntegrationTest {
	@Autowired
	private DatasetRegistrationController drc;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testControllerContext() {
		assertThat(drc).isNotNull();
	}

	@Test
	public void testFindDatasetByName () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/dataset_registration/by_name/testset1")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.group.email_address").value("testgroup@email.com"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	public void testFindDatasetByNameNotFound () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/dataset_registration/by_name/mydataset1")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(204));
	}
}
