package net.tgburrin.dasit.Dataset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

import net.tgburrin.dasit.InvalidDataException;
import net.tgburrin.dasit.Group.Group;

// https://spring.io/blog/2018/09/24/spring-data-jdbc-references-and-aggregates

enum DatasetStatus {
	ACTIVE,
	INACTIVE
	;
}

@Table(value = "dasit.datasets")
public class Dataset {
	@Id
	private Long id;

	private String name;
	@MappedCollection(idColumn = "id", keyColumn = "owner_group")
	//@Column("owner_group")
	private Group ownerGroup;
	private DatasetStatus status;

	Dataset () {
		this.id = null;
		this.ownerGroup = null;
		this.status = DatasetStatus.ACTIVE;
	}
	
	Dataset (String dsName, Group owner) {
		this.id = null;
		this.status = DatasetStatus.ACTIVE;

		this.name = dsName;
		this.ownerGroup = owner;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public Group getGroup() {
		return ownerGroup;
	}
	
	public void addGroup(Group g) {
		Assert.notNull(g, "Owner Group must not be null");
		Assert.notNull(g.getId(), "The Owner Group must be a populated object");
		
		ownerGroup = g;
	}
	
	public DatasetStatus getStatus() {
		return this.status;
	}

	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.id);
		s.add("Name: "+this.name);
		s.add("Owner: "+this.ownerGroup.getName());
		s.add("Owner Id: "+this.ownerGroup.getId());
		s.add("Status: "+this.status);
		
		return String.join("\n", s);
	}

	public void validateRecord() throws InvalidDataException {
		if(this.name == null || this.name.equals(""))
			throw new InvalidDataException("A valid dataset name must be specified");
	}
}