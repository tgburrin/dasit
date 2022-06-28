package net.tgburrin.dasit.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import net.tgburrin.dasit.InvalidDataException;

enum GroupStatus {
	ACTIVE,
	INACTIVE
	;
}

@Table(value = "groups")
public class Group {
	@Id
	private Long id;

	private String name;
	private String email;
	private GroupStatus status;

	public Group() {
		this.id = null;
		this.status = null;
	}

	public Group(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.status = GroupStatus.ACTIVE;
	}

	public long readId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String n) {
		this.name = n;
	}

	//@JsonProperty("email_address")
	public String getEmailAddress() {
		return this.email;
	}
	//@JsonProperty("email_address")
	public void setEmailAddress(String ea) {
		this.email = ea;
	}

	public String getStatus() {
		return this.status == null ? (String) null : this.status.toString();
	}

	public void setStatus(String s) throws InvalidDataException {
		if ( s == null || s.isEmpty() ) {
			this.setActive();
		} else {
			try {
				this.status = GroupStatus.valueOf(s);
			} catch ( java.lang.IllegalArgumentException e ) {
				throw new InvalidDataException("Invalid status '"+s+"'");
			}
		}
	}

	public boolean checkIsActive() {
		return this.status == GroupStatus.ACTIVE ? true : false;
	}

	public void setInactive() {
		this.status = GroupStatus.INACTIVE;
	}

	public void setActive() {
		this.status = GroupStatus.ACTIVE;
	}

	@Override
	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.id);
		s.add("Name: "+this.name);
		s.add("Email: "+this.email);
		s.add("Status: "+this.status);

		return String.join("\n", s);
	}

	public void validateRecord() throws InvalidDataException {
		if(this.name == null || this.name.equals(""))
			throw new InvalidDataException("A valid group name must be specified");

		if(this.email == null)
			throw new InvalidDataException("An email must be provided");

		if(!Pattern.matches(".*@.*.\\.*", this.email))
			throw new InvalidDataException("An invalid email address was used");
	}
}
