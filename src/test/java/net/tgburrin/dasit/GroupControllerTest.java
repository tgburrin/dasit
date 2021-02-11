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
    	.andExpect(MockMvcResultMatchers.jsonPath("$.email_address").value("testgroup@email.com"));
    }

    @Test
    public void testFindGroupByNameNotFound () throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/groups/by_name/mygroup1")
    			.accept(MediaType.APPLICATION_JSON))
    	.andDo(MockMvcResultHandlers.print())
    	.andExpect(MockMvcResultMatchers.status().is(204));
    }
}
