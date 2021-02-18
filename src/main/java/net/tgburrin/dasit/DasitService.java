package net.tgburrin.dasit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.tgburrin.dasit.Dataset.Dataset;
import net.tgburrin.dasit.Dataset.DatasetRepository;
import net.tgburrin.dasit.Dataset.DatasetWindow;
import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupRepository;

@Service
public class DasitService {
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private DatasetRepository datasetRepository;

	/* Group related calls */

	public Group findGroupByName(String name) throws NoRecordFoundException {
		Group g = groupRepository.findByName(name);
		if ( g == null )
			throw new NoRecordFoundException("Group '"+name+"' not found");

		return g;
	}

	public List<Group> findAllGroups() {
		List<Group> groups = new ArrayList<Group>();
		groupRepository.findAllActive().forEach(groups::add);
		return groups;
	}

	public Group findGroupById(Long id) {
		Optional<Group> group = groupRepository.findById(id);
		// This doesn't seem ideal, though the code using it does handle null values
		if (group.isPresent())
			return group.get();
		else
			return null;
	}

	public Group saveGroup(Group g) throws Exception {
		if ( g.getStatus() == "INACTIVE" ) {
			List<Dataset> datasetList = datasetRepository.findActiveByOwnerId(g.readId());
			if ( datasetList.size() > 0 )
				throw new InvalidDataException("You may not set a group with active datasets to be inactive");
		}
		g.validateRecord();
		groupRepository.save(g);
		return g;
	}

	/* Dataset related calls */

	public List<Dataset> findAllDatasets() {
		List<Dataset> dsList = new ArrayList<Dataset>();
		datasetRepository.findAllActive().forEach(dsList::add);
		return dsList;
	}

	public List<Dataset> findActiveDatasetsByGroupOwner(Long groupId) {
		List<Dataset> dsList = new ArrayList<Dataset>();
		datasetRepository.findActiveByOwnerId(groupId).forEach(dsList::add);
		return dsList;
	}

	public Dataset findDatasetByName(String n) throws NoRecordFoundException {
		Dataset d = datasetRepository.findByName(n);
		if ( d == null )
			throw new NoRecordFoundException("Dataset name '"+n+"' could not be found");

		return d;
	}

	public Dataset saveDataset(Dataset d) throws InvalidDataException {
		d.validateRecord();
		datasetRepository.save(d);
		return d;
	}

	/* Dataset window related calls */

	public List<DatasetWindow> findDatasetWindowsByName(String n) {
		return datasetRepository.findWindowsByName(n);
	}

	public DatasetWindow checkDatasetWindowExists(DatasetWindow dsw) throws NoRecordFoundException, InvalidDataException {
		dsw.validate();
		Dataset d = datasetRepository.findByName(dsw.datasetName);
		DatasetWindow found = datasetRepository.checkWindowExists(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));

		if ( found == null )
			throw new NoRecordFoundException("No Record Found");

		return found;
	}

	public DatasetWindow addDatasetPublishedWindow(DatasetWindow dsw) throws InvalidDataException {
		dsw.validate();

		Dataset d = datasetRepository.findByName(dsw.datasetName);
		return datasetRepository.addPublishedWindow(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));
	}

	public List<DatasetWindow> removeDatasetPublishedWindow(DatasetWindow dsw) throws InvalidDataException {
		dsw.validate();

		Dataset d = datasetRepository.findByName(dsw.datasetName);
		return datasetRepository.removePublishedWindow(d.readId(),
				Timestamp.from(dsw.getWindowStartDateTime()),
				Timestamp.from(dsw.getWindowEndDateTime()));
	}
}
