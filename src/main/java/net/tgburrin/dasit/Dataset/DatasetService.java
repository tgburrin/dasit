package net.tgburrin.dasit.Dataset;

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
}
