package net.tgburrin.dasit.Dataset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasetService {
	@Autowired
	private DatasetRepository dsRepo;

	public Dataset save(Dataset d) {
		dsRepo.save(d);
		return d;
	}

	public List<Dataset> findAll() {
		List<Dataset> dsList = new ArrayList<Dataset>();
		dsRepo.findAll().forEach(dsList::add);
		return dsList;
	}

	public Dataset findByName(String n) {
		return dsRepo.findByName(n);
	}
}
