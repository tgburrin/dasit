package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupService;

public class GroupServiceTest extends BaseIntegrationTest {
	@Autowired
	private GroupService groupService;

	@Test
	public void contextLoads() throws Exception {
		assertThat(groupService).isNotNull();
	}

	@Test
	public void findTestGroup() throws Exception {
		Group g = groupService.findByName("testgroup1");
		assertEquals("testgroup1", g.getName());
		assertEquals(1, g.readId());
	}

	@Test
	public void saveTestGroup() throws Exception {
		Group g = new Group(null, "NewGroup", "mybrokenemail.com");
		assertThrows(
				InvalidDataException.class, () -> {
					groupService.save(g);
				});

		g.setEmailAddress("myfixed@email.com");
		groupService.save(g);

		assertThat(g.getStatus()).isEqualTo("ACTIVE");
		assertThat(g.readId()).isEqualTo(101);
	}
}
