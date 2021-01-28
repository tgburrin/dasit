package net.tgburrin.dasit.Dataset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataset_publish")
public class DatasetPublishWindowController {
	@Autowired
	private DatasetService datasetService;

	@RequestMapping(value="/list_windows/{name}", method=RequestMethod.GET)
	public List<DatasetWindow> listGroups(@PathVariable("name") String name, Model m) throws Exception {
		return (List<DatasetWindow>) datasetService.findWindowsByName(name);
	}
}
