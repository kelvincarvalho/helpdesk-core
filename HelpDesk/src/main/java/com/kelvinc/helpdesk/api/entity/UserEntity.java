package com.kelvinc.helpdesk.api.entity;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.kelvinc.helpdesk.api.enums.ProfileEnum;

@Document
public class UserEntity implements Serializable{

	private static final long serialVersionUID = 8059863670068660111L;

	@Id
	private String id;
	
	@Indexed(unique = true)
	private String email;
	
	@Size(min = 6)
	private String password;
	
	private ProfileEnum profile;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ProfileEnum getProfile() {
		return profile;
	}

	public void setProfile(ProfileEnum profile) {
		this.profile = profile;
	}
	
	
}
