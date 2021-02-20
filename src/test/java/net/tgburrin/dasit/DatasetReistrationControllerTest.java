package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
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
		.andExpect(MockMvcResultMatchers.jsonPath("$.group.emailAddress").value("testgroup@email.com"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	public void testFindDatasetByNameNotFound () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/dataset_registration/by_name/mydataset1")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(204));
	}

	@Test
	public void testListDatasets () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/dataset_registration/list/")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray());
	}

	@Test
	public void testCreateDataset () throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "newDatasetName1")
		.put("ownerGroupName", "testgroup2");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_registration/create/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(201))
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("newDatasetName1"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.group.name").value("testgroup2"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.group.emailAddress").value("testgroup@email.com"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	public void testUpdateDataset () throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "isdeprecated1")
		.put("status", "INACTIVE");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_registration/update/by_name/todeprecate1")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("isdeprecated1"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.group.name").value("testgroup6"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("INACTIVE"));
	}
}
