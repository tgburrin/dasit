package net.tgburrin.dasit.Group;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ui.Model;

@RestController
@RequestMapping("/groups")
public class GroupController {
	
	@Autowired
	private GroupService groupService;

	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Group addGroup(@RequestBody Group g) throws Exception {
		groupService.save(g);
		return g;
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Group> listGroups() throws Exception {
		return groupService.findAll();
	}

	@RequestMapping(value="/group/{name}", method=RequestMethod.GET)
	public Group getGroup(String name, Model m) throws Exception {
		return groupService.findByName(name);
	}
}
