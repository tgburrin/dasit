package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupRepository;
import net.tgburrin.dasit.Group.GroupService;

public class GroupServiceTest extends BaseIntegrationTest {
	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupRepository groupRepository;

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
	public void findGroupsByEmail() {
		List<Group> gl = groupRepository.findByEmailAddress("testgroup@email.com");
		assertEquals(gl.size(), 2);

		gl = groupRepository.findByEmailAddress("nogroup@email.com");
		assertEquals(gl.size(), 0);
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
		assertThat(g.readId()).isGreaterThan(100);
	}

	@Test
	public void inactivateTestGroup() throws Exception {
		List<Group> gl = groupService.findAll();
		Group testGroup = null;

		for(Group g: gl) {
			if(g.getName().equals("testgroup3")) {
				testGroup = g;
			}
		}

		assertThat(testGroup).isNotNull();
		testGroup.setInactive();
		groupService.save(testGroup);

		testGroup = null;
		gl = groupService.findAll();
		for(Group g: gl) {
			if(g.getName().equals("testgroup3")) {
				testGroup = g;
			}
		}
		assertThat(testGroup).isNull();
	}
}
