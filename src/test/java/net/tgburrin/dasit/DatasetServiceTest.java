package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Dataset.Dataset;
import net.tgburrin.dasit.Dataset.DatasetRepository;
import net.tgburrin.dasit.Group.Group;

public class DatasetServiceTest extends BaseIntegrationTest {
	@Autowired
	private DasitService appService;

	@Autowired
	DatasetRepository datasetRepo;

	@Test
	public void contextLoads() throws Exception {
		assertThat(appService).isNotNull();
	}

	/*** Repository Tests ***/
	@Test
	public void findActiveDatasetsByGroup() {
		List<Dataset> dsList = datasetRepo.findActiveByOwnerId((long) 2);
		assertEquals(dsList.size(), 2);

		dsList = datasetRepo.findActiveByOwnerId((long) 3);
		assertEquals(dsList.size(), 0);
	}

	/*** Service Tests ***/
	@Test
	public void findDatasetByName () throws NoRecordFoundException {
		Dataset ds = appService.findDatasetByName("testset2");
		assertThat(ds).isNotNull();
		assertEquals(ds.getStatus(), "INACTIVE");
		assertEquals(ds.getGroup().getName(), "testgroup1");
	}

	@Test
	public void findDatasetByNameNotFound () {
		assertThrows(NoRecordFoundException.class, () -> {
			appService.findDatasetByName("testset0");
		});
	}

	@Test
	public void findAllFiltered () throws InvalidDataException {
		// concurrently running tests that commit could cause this to break
		// the number of active datasets could vary by more than we expect
		Dataset deprecatedDataset = null;

		List<Dataset> dsList = appService.findAllDatasets();
		long dsListStartingSize = dsList.size();
		for(Dataset ds: appService.findAllDatasets()) {
			if ( ds.getName().equals("deprecated1") )
				deprecatedDataset = ds;
		}

		assertThat(deprecatedDataset).isNotNull();
		deprecatedDataset.setInactive();
		appService.saveDataset(deprecatedDataset);

		deprecatedDataset = null;
		dsList = appService.findAllDatasets();
		for(Dataset ds: dsList) {
			if ( ds.getName().equals("deprecated1") )
				deprecatedDataset = ds;
		}
		assertThat(deprecatedDataset).isNull();
		assertEquals(dsList.size(), dsListStartingSize - 1);
	}

	@Test
	public void addDataset() throws Exception {
		Group ownerGroup = appService.findGroupByName("testgroup7");
		Dataset newDs = new Dataset("", ownerGroup);

		assertThrows(InvalidDataException.class, () -> {
			appService.saveDataset(newDs);
		});

		newDs.setName("testset4");
		appService.saveDataset(newDs);

		assertThat(newDs.readId()).isGreaterThan(100);
	}
}
