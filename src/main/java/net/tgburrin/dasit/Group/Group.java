package net.tgburrin.dasit.Group;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

enum GroupStatus {
	ACTIVE,
	INACTIVE
	;
}

@Table(value = "dasit.groups")
public class Group {
	@Id
	private Long id;
	
	@Column(value = "name")
	private String name;

	@Column(value = "email")
	private String emailAddress;
	
	@Column(value = "status")
	private GroupStatus status;

	Group() {
		this.id = null;
		this.status = GroupStatus.ACTIVE;
	}
	
	Group(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.emailAddress = email;
		this.status = GroupStatus.ACTIVE;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String n) {
		this.name = n;
	}

	@JsonProperty("email_address")
	public String getEmailAddress() {
		return this.emailAddress;
	}
	@JsonProperty("email_address")
	public void setEmailAddress(String ea) {
		this.emailAddress = ea;
	}

	public String getStatus() {
		return this.status.toString();
	}
	public void setStatus(String s) {
		this.status = GroupStatus.valueOf(s);
	}
	public void setInactive() {
		this.status = GroupStatus.INACTIVE;
	}
	public void setActive() {
		this.status = GroupStatus.ACTIVE;
	}
	
	public String toString() {
		List<String> s = new ArrayList<String>();
		s.add("Id: "+this.id);
		s.add("Name: "+this.name);
		s.add("Email: "+this.emailAddress);
		s.add("Status: "+this.status);
		
		return String.join("\n", s);
	}

	public void validateRecord() throws Exception {
		if(!Pattern.matches(".*@.*.\\.*", this.emailAddress))
			throw new Exception("An invalid email address was used");
	}
}
