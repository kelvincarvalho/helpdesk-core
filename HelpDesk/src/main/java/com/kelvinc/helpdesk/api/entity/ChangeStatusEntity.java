package com.kelvinc.helpdesk.api.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.kelvinc.helpdesk.api.enums.StatusEnum;

@Document
public class ChangeStatusEntity implements Serializable {

	private static final long serialVersionUID = -714784107362577497L;

	@Id
	private String id;

	@DBRef
	private TicketEntity ticket;

	@DBRef
	private UserEntity userChange;

	private Date dateChangeStatus;

	private StatusEnum status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TicketEntity getTicket() {
		return ticket;
	}

	public void setTicket(TicketEntity ticket) {
		this.ticket = ticket;
	}

	public UserEntity getUserChange() {
		return userChange;
	}

	public void setUserChange(UserEntity user) {
		this.userChange = user;
	}

	public Date getDateChangeStatus() {
		return dateChangeStatus;
	}

	public void setDateChangeStatus(Date dateChangeStatus) {
		this.dateChangeStatus = dateChangeStatus;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

}
