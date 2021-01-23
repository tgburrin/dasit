package net.tgburrin.dasit.Dataset;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AfterLoadCallback;
import org.springframework.stereotype.Component;

import net.tgburrin.dasit.Group.Group;
import net.tgburrin.dasit.Group.GroupRepository;

@Component
public class DatasetCallbacks implements AfterLoadCallback<Dataset> {
	@Autowired
	private GroupRepository gr;

	@Override
	public Dataset onAfterLoad(Dataset d) {
		Optional<Group> g = gr.findById(d.readGroupId());
		d.setGroupId(g.get());
		return d;
	}

}
