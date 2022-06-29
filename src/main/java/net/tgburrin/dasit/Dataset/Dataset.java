package net.tgburrin.dasit.Dataset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.Assert;

import net.tgburrin.dasit.InvalidDataException;
import net.tgburrin.dasit.StatusEnum;
import net.tgburrin.dasit.Group.Group;

// https://spring.io/blog/2018/09/24/spring-data-jdbc-references-and-aggregates

@Table(value = "datasets")
public class Dataset {
	@Id
	private Long id;

	private String name;

	@Column("owner_group")
	private Long ownerGroupId;

	@Transient
	private Group ownerGroup;

	private StatusEnum status;

	public Dataset () {
		this.id = null;
		this.ownerGroupId = null;
		this.status = StatusEnum.ACTIVE;
	}

	public Dataset (String dsName, Group owner) {
		this.id = null;
		this.status = StatusEnum.ACTIVE;

		this.name = dsName;
		this.setGroupId(owner);
	}

	public long readId() {
		return id;
	}

	protected void setId(Long i) {
		this.id = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public Long readGroupId() {
		return this.ownerGroupId;
	}

	public Group getGroup() {
		return ownerGroup;
	}

	public void setGroupId(Group g) {
		Assert.notNull(g, "Owner Group must not be null");
		Assert.notNull(g.readId(), "The Owner Group must be a populated object");

		this.ownerGroup = g;
		this.ownerGroupId = g.readId();
	}

	public String getStatus() {
		return this.status.toString();
	}

	public void setStatus(String s) throws InvalidDataException {
		try {
			this.status = StatusEnum.valueOf(s);
		} catch ( java.lang.IllegalArgumentException e ) {
			throw new InvalidDataException("Invalid status '"+s+"'");
		}
	}

	public boolean checkIsActive() {
		return this.status == StatusEnum.ACTIVE ? true : false;
	}

	public void setActive() {
		this.status = StatusEnum.ACTIVE;
	}

	public void setInactive() {
		this.status = StatusEnum.INACTIVE;
	}

	@Override
	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.id);
		s.add("Name: "+this.name);
		s.add("Owner Id: "+this.ownerGroupId);
		s.add("Status: "+this.status);

		return String.join("\n", s);
	}

	public void validateRecord() throws InvalidDataException {
		if( this.status == StatusEnum.ACTIVE && !this.ownerGroup.checkIsActive() )
			throw new InvalidDataException("A dataset can only be active if the group is active");

		if( this.name == null || this.name.equals("") )
			throw new InvalidDataException("A valid dataset name must be specified");
	}
}