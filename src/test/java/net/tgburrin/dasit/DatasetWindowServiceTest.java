package net.tgburrin.dasit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.tgburrin.dasit.Dataset.Dataset;
import net.tgburrin.dasit.Dataset.DatasetRepository;
import net.tgburrin.dasit.Dataset.DatasetWindow;

public class DatasetWindowServiceTest extends BaseIntegrationTest {
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
	public void checkWindowExistsNotFoundRepo() throws NoRecordFoundException {
		Dataset ds = appService.findDatasetByName("testset1");
		Instant startTime = Instant.parse("2020-08-24T00:00:00.00Z");
		Instant endTime = Instant.parse("2020-08-26T00:00:00.00Z");

		assertThat(datasetRepo.checkWindowExists(
				ds.readId(),
				Timestamp.from(startTime),
				Timestamp.from(endTime))).isNull();
	}

	@Test
	public void checkWindowExistsIsFoundRepo() throws NoRecordFoundException {
		Instant startTime = Instant.parse("2020-08-24T00:00:00.00Z");
		Instant endTime = Instant.parse("2020-08-25T00:00:00.00Z");

		assertThat(datasetRepo.checkWindowExists(
				(long) 1,
				Timestamp.from(startTime),
				Timestamp.from(endTime))).isNotNull();
	}

	/*** Service Tests ***/
	@Test
	public void findDatasetWindowsByName() {
		List<DatasetWindow> dsw = appService.findDatasetWindowsByName("testset1");
		assertThat(dsw.size()).isEqualTo(3);
	}

	@Test
	public void checkWindowExistsNotFoundService() throws NoRecordFoundException {
		DatasetWindow dw = new DatasetWindow();
		dw.datasetName = "testset1";
		dw.setWindowStartDateTime(Instant.parse("2020-08-24T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-08-26T00:00:00.00Z"));

		assertThrows(NoRecordFoundException.class, () -> {
			appService.checkDatasetWindowExists(dw);
		});
	}

	@Test
	public void checkWindowExistsIsFoundService() throws NoRecordFoundException, InvalidDataException {
		DatasetWindow dw = new DatasetWindow();
		dw.datasetName = "testset1";
		dw.setWindowStartDateTime(Instant.parse("2020-08-24T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-08-25T00:00:00.00Z"));

		dw = appService.checkDatasetWindowExists(dw);
		/* check that the start time is lower than what we asked for since the window is satisfied by a larger range */
		assertThat(dw.getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-23T00:00:00.00Z"));
		assertThat(dw.getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-25T00:00:00.00Z"));
	}

	@Test
	public void publishWindows() throws InvalidDataException {
		DatasetWindow dw = new DatasetWindow();
		dw.datasetName = "testset3";
		dw.setWindowStartDateTime(Instant.parse("2020-05-17T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-05-18T00:00:00.00Z"));

		dw = appService.addDatasetPublishedWindow(dw);
		assertThat(dw.getWindowStartDateTime()).isEqualTo(Instant.parse("2020-05-17T00:00:00.00Z"));
		assertThat(dw.getWindowEndDateTime()).isEqualTo(Instant.parse("2020-05-18T00:00:00.00Z"));

		dw = new DatasetWindow();
		dw.datasetName = "testset3";
		dw.setWindowStartDateTime(Instant.parse("2020-05-18T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-05-19T00:00:00.00Z"));

		dw = appService.addDatasetPublishedWindow(dw);
		assertThat(dw.getWindowStartDateTime()).isEqualTo(Instant.parse("2020-05-17T00:00:00.00Z"));
		assertThat(dw.getWindowEndDateTime()).isEqualTo(Instant.parse("2020-05-19T00:00:00.00Z"));
	}

	@Test
	public void publishWindowMerge() throws InvalidDataException {
		DatasetWindow dw = new DatasetWindow();
		dw.datasetName = "testset3";
		// Will glue two ranges on either side together and remove one segment in the middle
		dw.setWindowStartDateTime(Instant.parse("2020-11-14T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-11-18T00:00:00.00Z"));

		dw = appService.addDatasetPublishedWindow(dw);
		assertThat(dw.getWindowStartDateTime()).isEqualTo(Instant.parse("2020-11-13T00:00:00.00Z"));
		assertThat(dw.getWindowEndDateTime()).isEqualTo(Instant.parse("2020-11-20T00:00:00.00Z"));
	}

	@Test
	public void removeWindow() throws InvalidDataException {
		DatasetWindow dw = new DatasetWindow();
		dw.datasetName = "testset3";
		// Will glue two ranges on either side together and remove one segment in the middle
		dw.setWindowStartDateTime(Instant.parse("2020-08-16T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-08-17T00:00:00.00Z"));

		List<DatasetWindow> dwList = appService.removeDatasetPublishedWindow(dw);
		// returns an ordered list where the oldest neighbor window is earlier
		assertEquals(dwList.size(), 2);

		assertThat(dwList.get(0).getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-11T00:00:00.00Z"));
		assertThat(dwList.get(0).getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-16T00:00:00.00Z"));

		assertThat(dwList.get(1).getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-17T00:00:00.00Z"));
		assertThat(dwList.get(1).getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-22T00:00:00.00Z"));

		dw = new DatasetWindow();
		dw.datasetName = "testset3";
		dw.setWindowStartDateTime(Instant.parse("2020-08-01T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-08-13T00:00:00.00Z"));

		dwList = appService.removeDatasetPublishedWindow(dw);
		// returns an ordered list where the oldest neighbor window is earlier
		assertEquals(dwList.size(), 1);
		assertThat(dwList.get(0).getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-13T00:00:00.00Z"));
		assertThat(dwList.get(0).getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-16T00:00:00.00Z"));

		dw = new DatasetWindow();
		dw.datasetName = "testset3";
		dw.setWindowStartDateTime(Instant.parse("2020-08-16T00:00:00.00Z"));
		dw.setWindowEndDateTime(Instant.parse("2020-08-17T00:00:00.00Z"));
		// noop removal that does not complain and returns the neighboring segments
		dwList = appService.removeDatasetPublishedWindow(dw);
		assertEquals(dwList.size(), 2);

		assertThat(dwList.get(0).getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-13T00:00:00.00Z"));
		assertThat(dwList.get(0).getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-16T00:00:00.00Z"));

		assertThat(dwList.get(1).getWindowStartDateTime()).isEqualTo(Instant.parse("2020-08-17T00:00:00.00Z"));
		assertThat(dwList.get(1).getWindowEndDateTime()).isEqualTo(Instant.parse("2020-08-22T00:00:00.00Z"));
	}
}
