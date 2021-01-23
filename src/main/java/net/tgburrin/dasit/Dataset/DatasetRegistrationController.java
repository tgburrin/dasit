package net.tgburrin.dasit.Dataset;

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

import net.tgburrin.dasit.InvalidDataException;
import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupService;

@RestController
@RequestMapping("/dataset_registration")
public class DatasetRegistrationController {
	@Autowired
	private DatasetService datasetService;

	@Autowired
	private GroupService grpService;

	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Dataset createDataset(@RequestBody DatasetCreateRequest dsReq) throws InvalidDataException {
		Group g = grpService.findByName(dsReq.ownerGroupName);
		Dataset d = new Dataset(dsReq.datasetName, g);
		d.validateRecord();
		datasetService.save(d);
		return d;
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Dataset> listGroups() throws Exception {
		return datasetService.findAll();
	}

	@RequestMapping(value="/by_name/{name}", method=RequestMethod.GET)
	public Dataset getDataset(@PathVariable("name") String name, Model m) throws Exception {
		return datasetService.findByName(name);
	}
}
