package net.tgburrin.dasit.Dataset;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import net.tgburrin.dasit.InvalidDataException;

@Table(value = "datasets_published")
public class DatasetWindow {
	@Id
	@Column("dataset_id") Long datasetId;
	@Column("publish_start_dt") Instant startDt;
	@Column("publish_end_dt") Instant endDt;

	@Transient
	public String datasetName;

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

	public void validate() throws InvalidDataException {
		if ( datasetName == null )
			throw new InvalidDataException("A dataset name must be provided");

		if ( startDt == null || endDt == null )
			throw new InvalidDataException("A dataset start and end time must be provided");
	}
}
