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

@RestController
@RequestMapping("/dataset_publish")
public class DatasetPublishWindowController {
	@Autowired
	private DasitService appService;

	@RequestMapping(value="/list_windows/{name}", method=RequestMethod.GET)
	public List<DatasetWindow> listGroups(@PathVariable("name") String name, Model m) throws Exception {
		return appService.findDatasetWindowsByName(name);
	}

	@PostMapping(value="/check_window", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public DatasetWindow checkWindow(@RequestBody DatasetWindow dsc) throws Exception {
		return appService.checkDatasetWindowExists(dsc);
	}

	@PostMapping(value="/publish_window", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public DatasetWindow addWindow(@RequestBody DatasetWindow dsc) throws Exception {
		return appService.addDatasetPublishedWindow(dsc);
	}

	@PostMapping(value="/remove_window", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public List<DatasetWindow> removeWindow(@RequestBody DatasetWindow dsc) throws Exception {
		return appService.removeDatasetPublishedWindow(dsc);
	}
}
