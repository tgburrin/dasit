package net.tgburrin.dasit.Group;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

	@RequestMapping(value="/by_name/{name}", method=RequestMethod.GET)
	public Group getGroupByName(@PathVariable("name") String name, Model m) throws Exception {
		return groupService.findByName(name);
	}

	@RequestMapping(value="/by_id/{id}", method=RequestMethod.GET)
	public Group getGroupById(@PathVariable("id") Long id, Model m) throws Exception {
		return groupService.findById(id);
	}
}
