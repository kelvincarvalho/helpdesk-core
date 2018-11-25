package com.kelvinc.helpdesk.api.service;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.kelvinc.helpdesk.api.entity.ChangeStatusEntity;
import com.kelvinc.helpdesk.api.entity.TicketEntity;
import com.kelvinc.helpdesk.api.response.Response;

@Component
public interface TicketService {

	ResponseEntity<Response<TicketEntity>> createTicket(TicketEntity ticket, BindingResult result, HttpServletRequest request);

	ResponseEntity<Response<TicketEntity>> updateTicket(TicketEntity ticket, BindingResult result, HttpServletRequest request);
	
	ResponseEntity<Response<TicketEntity>> findById(String id);

	ResponseEntity<Response<String>> delete(String id);

	ResponseEntity<Response<Page<TicketEntity>>> listTicket(int page, int count, HttpServletRequest request);
	
	ResponseEntity<Response<TicketEntity>> createChangeStatus(String id, String status, BindingResult result, HttpServletRequest request);

	Iterable<ChangeStatusEntity> listChangeStatus(String ticketId);

	Page<TicketEntity> findByCurrentUser(int page, int count, String userId);

	Page<TicketEntity> findByParameters(int page, int count, String title, String status, String priority);

	Page<TicketEntity> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority,
			String userId);

	Page<TicketEntity> findByNumber(int page, int count, Integer number);

	Iterable<TicketEntity> findAll();

	public Page<TicketEntity> findByParametersAndAssignedUser(int page, int count, String title, String status,
			String priority, String assignedUserId);


}
