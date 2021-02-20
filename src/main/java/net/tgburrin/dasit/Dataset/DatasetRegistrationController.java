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

import net.tgburrin.dasit.DasitService;
import net.tgburrin.dasit.InvalidDataException;
import net.tgburrin.dasit.NoRecordFoundException;
import net.tgburrin.dasit.Group.Group;

@RestController
@RequestMapping("/dataset_registration")
public class DatasetRegistrationController {
	@Autowired
	private DasitService appService;

	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Dataset createDataset(@RequestBody DatasetCreateRequest dsReq) throws InvalidDataException, NoRecordFoundException {
		Group g = null;
		try {
			g = appService.findGroupByName(dsReq.ownerGroupName);
		} catch (NoRecordFoundException e) {
			throw new InvalidDataException("Group '"+dsReq.ownerGroupName+"' could not be found");
		}

		Dataset d = new Dataset(dsReq.datasetName, g);
		d.validateRecord();
		appService.saveDataset(d);
		return d;
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Dataset> listDatasets() throws Exception {
		return appService.findAllDatasets();
	}

	@RequestMapping(value="/by_name/{name}", method=RequestMethod.GET)
	public Dataset getDataset(@PathVariable("name") String name, Model m) throws Exception {
		return appService.findDatasetByName(name);
	}

	@RequestMapping(value="/by_owner/{name}", method=RequestMethod.GET)
	public List<Dataset> getDatasetsByOwner(@PathVariable("name") String name, Model m) throws Exception {
		Group g = null;
		try {
			g = appService.findGroupByName(name);
		} catch (NoRecordFoundException e) {
			throw new InvalidDataException("Group '"+name+"' could not be found");
		}
		return appService.findDatasetsByGroupOwner(g.readId());
	}

	@PostMapping(value="/update/by_name/{name}", consumes = "application/json", produces = "application/json")
	public Dataset updateDatasetByName(@PathVariable("name") String name, @RequestBody DatasetUpdateRequest dsReq) throws NoRecordFoundException, InvalidDataException {
		return appService.updateDatasetByName(name, dsReq);
	}
}
