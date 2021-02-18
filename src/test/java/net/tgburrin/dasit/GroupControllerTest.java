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

import net.tgburrin.dasit.Group.GroupController;

@AutoConfigureMockMvc
public class GroupControllerTest extends BaseIntegrationTest {
	@Autowired
	private GroupController gc;

	@Autowired
	private MockMvc mvc;

	@Test
	public void testControllerContext() {
		assertThat(gc).isNotNull();
	}

	@Test
	public void testFindGroupByName () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/groups/by_name/testgroup1")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.emailAddress").value("testgroup@email.com"));
	}

	@Test
	public void testFindGroupByNameNotFound () throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/groups/by_name/mygroup1")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(204));
	}

	@Test
	public void testListGroups() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/groups/list/")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray());
	}

	@Test
	public void testCreateGroup () throws Exception {
		JSONObject req = new JSONObject();
		req
		.put("name", "newGroupName1")
		.put("emailAddress", "newgroupname1@email.com");

		mvc.perform(MockMvcRequestBuilders
				.post("/groups/create/")
				.content(req.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.status().is(201))
		.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("newGroupName1"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.emailAddress").value("newgroupname1@email.com"))
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"));
	}
}
