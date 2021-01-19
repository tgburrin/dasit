package net.tgburrin.dasit.Dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataset_registration")
public class DatasetRegistrationController {
	@Autowired
	private DatasetService datasetService;
	
	@PostMapping(value="/create", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Dataset createDataset(@RequestBody Dataset ds) {
		System.out.println("Dataset: "+ds.toString());
		return ds;
	}
}
