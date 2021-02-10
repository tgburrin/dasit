package net.tgburrin.dasit.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
	@Autowired
	private GroupRepository groupRepository;

	public Group save(Group g) throws Exception {
		g.validateRecord();
		groupRepository.save(g);
		return g;
	}

	public Group findByName(String name) {
		return groupRepository.findByName(name);
	}

	public List<Group> findAll() {
		List<Group> groups = new ArrayList<Group>();
		groupRepository.findAllActive().forEach(groups::add);
		return groups;
	}

	public Group findById(Long id) {
		Optional<Group> group = groupRepository.findById(id);
		return group.get();
	}
}
