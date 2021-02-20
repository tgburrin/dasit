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

import net.tgburrin.dasit.DasitService;

@RestController
@RequestMapping("/groups")
public class GroupController {
	@Autowired
	private DasitService appService;

	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Group addGroup(@RequestBody Group g) throws Exception {
		if(g.getStatus() == null)
			g.setActive();

		appService.saveGroup(g);
		return g;
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Group> listGroups() throws Exception {
		return appService.findAllGroups();
	}

	@RequestMapping(value="/by_name/{name}", method=RequestMethod.GET)
	public Group getGroupByName(@PathVariable("name") String name, Model m) throws Exception {
		return appService.findGroupByName(name);
	}

	@PostMapping(value="/update/by_name/{name}", consumes = "application/json", produces = "application/json")
	public Group updateGroupByName(@PathVariable("name") String name, @RequestBody Group g) throws Exception {
		return appService.updateGroupByName(name, g);
	}
}
