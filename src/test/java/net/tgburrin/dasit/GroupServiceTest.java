package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Group.GroupService;

public class GroupServiceTest extends BaseIntegrationTest {
	@Autowired
	private GroupService groupService;

	@Test
	public void contextLoads() throws Exception {
		assertThat(groupService).isNotNull();
	}

	@Test
	public void saveTestGroup() throws Exception {
		//Group g = new Group();
		//groupService.save(g);
	}
}
