package net.tgburrin.dasit.Dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AfterLoadCallback;
import org.springframework.stereotype.Component;

import net.tgburrin.dasit.DasitService;
import net.tgburrin.dasit.Group.Group;

@Component
public class DatasetCallbacks implements AfterLoadCallback<Dataset> {
	@Autowired
	private DasitService appService;

	@Override
	public Dataset onAfterLoad(Dataset d) {
		Group g = appService.findGroupById(d.readGroupId());
		d.setGroupId(g);
		return d;
	}

}
