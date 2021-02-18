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

	@Test
	public void testCheckPublishedWindow () throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset1")
		.put("windowStartDateTime", "2020-08-11T00:00:00Z")
		.put("windowEndDateTime", "2020-08-12T00:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/check_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowStartDateTime").value("2020-08-01T00:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowEndDateTime").value("2020-08-12T00:00:00Z"));
	}

	@Test
	public void testCheckPublishedWindowNotFound () throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset1")
		.put("windowStartDateTime", "2020-07-31T00:00:00Z")
		.put("windowEndDateTime", "2020-08-02T00:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/check_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(204));
	}

	@Test
	public void testPublishDatasetWindow() throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset3")
		.put("windowStartDateTime", "2020-03-15T00:00:00Z")
		.put("windowEndDateTime", "2020-03-28T00:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/publish_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.datasetName").value("testset3"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowStartDateTime").value("2020-03-15T00:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowEndDateTime").value("2020-03-28T00:00:00Z"));
	}

	@Test
	public void testPublishOverlapDatasetWindow() throws Exception {
		// The following window fills in a gap and glues two ranges together
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset3")
		.put("windowStartDateTime", "2020-11-15T00:00:00Z")
		.put("windowEndDateTime", "2020-11-16T00:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/publish_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.datasetName").value("testset3"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowStartDateTime").value("2020-11-13T00:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.windowEndDateTime").value("2020-11-17T00:00:00Z"));
	}

	@Test
	public void testRemoveDatasetWindow() throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset3")
		.put("windowStartDateTime", "2020-01-17T06:00:00Z")
		.put("windowEndDateTime", "2020-01-18T12:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/remove_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray())
		.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].windowStartDateTime").value("2020-01-15T00:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].windowEndDateTime").value("2020-01-17T06:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$[1].windowStartDateTime").value("2020-01-18T12:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$[1].windowEndDateTime").value("2020-01-20T00:00:00Z"));
	}

	@Test
	public void testRemoveOverlapDatasetWindow() throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("datasetName", "testset3")
		.put("windowStartDateTime", "2020-01-29T00:00:00Z")
		.put("windowEndDateTime", "2020-02-02T00:00:00Z");

		mvc.perform(MockMvcRequestBuilders
				.post("/dataset_publish/remove_window/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray())
		.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].windowStartDateTime").value("2020-01-25T00:00:00Z"))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].windowEndDateTime").value("2020-01-29T00:00:00Z"));
	}
}
