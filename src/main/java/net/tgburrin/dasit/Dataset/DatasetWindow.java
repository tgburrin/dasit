package net.tgburrin.dasit.Dataset;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "dasit.datasets_published")
public class DatasetWindow {
	@Id
	@Column("dataset_id") Long datasetid;
	@Column("publish_start_dt") Instant startDt;
	@Column("publish_end_dt") Instant endDt;

	@Transient String datasetName;

	public String getDatasetName() {
		return datasetName;
	}

	public Instant getWindowStartDateTime() {
		return startDt;
	}

	public Instant getWindowEndDateTime() {
		return endDt;
	}
}
