package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupRepository;

public class GroupServiceTest extends BaseIntegrationTest {
	@Autowired
	private DasitService appService;

	@Autowired
	private GroupRepository groupRepository;

	@Test
	public void contextLoads() throws Exception {
		assertThat(appService).isNotNull();
	}

	/*** Repository Tests ***/
	@Test
	public void findGroupsByEmail() {
		List<Group> gl = groupRepository.findByEmailAddress("testgroup@email.com");
		assertEquals(gl.size(), 2);

		gl = groupRepository.findByEmailAddress("nogroup@email.com");
		assertEquals(gl.size(), 0);
	}

	/*** Service Tests ***/
	@Test
	public void findGroupById() {
		Group g = appService.findGroupById((long) 1);
		assertThat(g).isNotNull();
		assertEquals(g.getName(), "testgroup1");

		g = appService.findGroupById((long) 0);
		assertThat(g).isNull();
	}

	@Test
	public void findGroupByName() throws Exception {
		Group g = appService.findGroupByName("testgroup1");
		assertEquals("testgroup1", g.getName());
		assertEquals(1, g.readId());
	}

	@Test
	public void findAllGroups() {
		List<Group> gl = appService.findAllGroups();
		assertThat(gl.size()).isGreaterThan(0);

		for(Group g: gl) {
			assertThat(g.checkIsActive()).isTrue();
		}
	}

	@Test
	public void saveGroup() throws Exception {
		Group g = new Group(null, "NewGroup", "mybrokenemail.com");
		assertThrows(
				InvalidDataException.class, () -> {
					appService.saveGroup(g);
				});

		g.setEmailAddress("myfixed@email.com");
		appService.saveGroup(g);

		assertThat(g.getStatus()).isEqualTo("A");
		assertThat(g.readId()).isGreaterThan(100);
	}

	@Test
	public void inactivateGroupWithActiveDatasets() {
		Group g = groupRepository.findByName("testgroup2");
		g.setInactive();
		assertThrows(InvalidDataException.class, () -> {
			appService.saveGroup(g);
		});
	}

	@Test
	public void inactivateGroup() throws Exception {
		List<Group> gl = appService.findAllGroups();
		Group testGroup = null;

		for(Group g: gl) {
			if(g.getName().equals("testgroup3")) {
				testGroup = g;
			}
		}

		assertThat(testGroup).isNotNull();
		testGroup.setInactive();
		appService.saveGroup(testGroup);

		testGroup = null;
		gl = appService.findAllGroups();
		for(Group g: gl) {
			if(g.getName().equals("testgroup3")) {
				testGroup = g;
			}
		}
		assertThat(testGroup).isNull();
	}
}
