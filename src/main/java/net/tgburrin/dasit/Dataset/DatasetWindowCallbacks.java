package net.tgburrin.dasit.Dataset;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AfterLoadCallback;
import org.springframework.stereotype.Component;

@Component
public class DatasetWindowCallbacks implements AfterLoadCallback<DatasetWindow> {
	@Autowired
	private DatasetRepository dr;

	@Override
	public DatasetWindow onAfterLoad(DatasetWindow d) {
		Optional<Dataset> dataSet = dr.findById(d.datasetId);
		d.datasetName = dataSet.get().getName();
		return d;
	}

}
