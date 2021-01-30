package net.tgburrin.dasit.Dataset;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "dasit.datasets_published")
public class DatasetWindow {
	@Id
	@Column("dataset_id") Long datasetId;
	@Column("publish_start_dt") Instant startDt;
	@Column("publish_end_dt") Instant endDt;

	@Transient String datasetName;

	public String getDatasetName() {
		return datasetName;
	}

	public Instant getWindowStartDateTime() {
		return startDt;
	}

	public void setWindowStartDateTime(Instant sd) {
		this.startDt = sd;
	}

	public Instant getWindowEndDateTime() {
		return endDt;
	}

	public void setWindowEndDateTime(Instant ed) {
		this.endDt = ed;
	}

	@Override
	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.datasetId);
		s.add("Name: "+this.datasetName);
		s.add("Start Datetime: "+this.startDt);
		s.add("End Datetime: "+this.endDt);

		return String.join("\n", s);
	}
}
