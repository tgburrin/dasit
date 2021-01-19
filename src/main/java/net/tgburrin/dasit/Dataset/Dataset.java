package net.tgburrin.dasit.Dataset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.util.Assert;

import net.tgburrin.dasit.Group.Group;

// https://spring.io/blog/2018/09/24/spring-data-jdbc-references-and-aggregates

enum DatasetStatus {
	ACTIVE,
	INACTIVE
	;
}

public class Dataset {
	@Id
	private Long id;
	
	@Column(value = "name")
	private String datasetName;

	/*
	@Column(value = "group_owner")
	private Long groupOwner;
	*/
	
	@Column(value = "owner_group")
	private Group ownerGroup;
	
	@Column(value = "status")
	private DatasetStatus status;

	Dataset () {
		this.id = null;
		this.status = DatasetStatus.ACTIVE;
	}
	
	Dataset (Long id, String dsName, Group owner) {
		this.id = id;
		this.datasetName = dsName;
		this.ownerGroup = owner;
		this.status = DatasetStatus.ACTIVE;
	}

	public long getId() {
		return id;
	}

	public String getdDatasetName() {
		return datasetName;
	}
	
	public void setDatasetName(String n) {
		datasetName = n;
	}
	
	public Group getGroupOwnerId() {
		return ownerGroup;
	}
	
	public void setGroupOwnerId(Group g) {
		Assert.notNull(g, "Owner Group must not be null");
		Assert.notNull(g.getId(), "The Owner Group must be a populated object");
		
		ownerGroup = g;
	}
	
	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.id);
		s.add("Name: "+this.datasetName);
		s.add("Owner: "+this.ownerGroup.getName());
		s.add("Status: "+this.status);
		
		return String.join("\n", s);
	}

}