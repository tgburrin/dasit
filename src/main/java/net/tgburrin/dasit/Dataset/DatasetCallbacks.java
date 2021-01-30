package net.tgburrin.dasit.Dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AfterLoadCallback;
import org.springframework.stereotype.Component;

import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupService;

@Component
public class DatasetCallbacks implements AfterLoadCallback<Dataset> {
	@Autowired
	private GroupService gs;

	@Override
	public Dataset onAfterLoad(Dataset d) {
		Group g = gs.findById(d.readGroupId());
		d.setGroupId(g);
		return d;
	}

}
