package com.kelvinc.helpdesk.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.kelvinc.helpdesk.api.enums.PriorityEnum;
import com.kelvinc.helpdesk.api.enums.StatusEnum;

@Document
public class TicketEntity implements Serializable {

	private static final long serialVersionUID = 5447420094322804186L;

	@Id
	private String id;

	@DBRef(lazy = true)
	private UserEntity user;

	private Date date;

	private String title;

	private Integer number;

	private StatusEnum status;

	private PriorityEnum priority;

	@DBRef(lazy = true)
	private UserEntity assignedUser;

	private String description;

	private String image;
	
	//Notação para não ter representação no banco de dados
	@Transient
	private List<ChangeStatusEntity> changes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public PriorityEnum getPriority() {
		return priority;
	}

	public void setPriority(PriorityEnum priority) {
		this.priority = priority;
	}

	public UserEntity getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(UserEntity assignedUser) {
		this.assignedUser = assignedUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<ChangeStatusEntity> getChanges() {
		return changes;
	}

	public void setChanges(List<ChangeStatusEntity> changes) {
		this.changes = changes;
	}
}
