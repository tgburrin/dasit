package net.tgburrin.dasit.Group;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
	@Autowired
	private GroupRepository groupRepository;
	
	public Group save(Group g) {
		groupRepository.save(g);
		return g;
	}
	
	public Group findByName(String name) {
		return groupRepository.findByName(name);
	}
	
	public List<Group> findAll() {
		List<Group> groups = new ArrayList<Group>();
		groupRepository.findAll().forEach(groups::add);
		return groups;
	}
}
