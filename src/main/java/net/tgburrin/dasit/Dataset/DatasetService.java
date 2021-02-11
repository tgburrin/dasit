package net.tgburrin.dasit.Dataset;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.tgburrin.dasit.NoRecordFoundException;

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
		dsRepo.findAllActive().forEach(dsList::add);
		return dsList;
	}

	public Dataset findByName(String n) {
		return dsRepo.findByName(n);
	}

	public List<DatasetWindow> findWindowsByName(String n) {
		return dsRepo.findWindowsByName(n);
	}

	public DatasetWindow checkWindowExists(DatasetWindow dsw) throws NoRecordFoundException {
		Dataset d = dsRepo.findByName(dsw.datasetName);
		DatasetWindow found = dsRepo.checkWindowExists(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));

		if ( found == null )
			throw new NoRecordFoundException("No Record Found");

		return found;
	}

	public DatasetWindow addPublishedWindow(DatasetWindow dsw) {
		Dataset d = dsRepo.findByName(dsw.datasetName);
		return dsRepo.addPublishedWindow(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));
	}

	public List<DatasetWindow> removePublishedWindow(DatasetWindow dsw) {
		Dataset d = dsRepo.findByName(dsw.datasetName);
		return dsRepo.removePublishedWindow(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));
	}
}
