package com.kelvinc.helpdesk.api.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.kelvinc.helpdesk.api.entity.ChangeStatusEntity;
import com.kelvinc.helpdesk.api.entity.TicketEntity;
import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.enums.ProfileEnum;
import com.kelvinc.helpdesk.api.enums.StatusEnum;
import com.kelvinc.helpdesk.api.repository.ChangeStatusRepository;
import com.kelvinc.helpdesk.api.repository.TicketRepository;
import com.kelvinc.helpdesk.api.response.Response;
import com.kelvinc.helpdesk.api.security.jwt.JwtTokenUtil;
import com.kelvinc.helpdesk.api.service.TicketService;
import com.kelvinc.helpdesk.api.service.UserService;

@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private ChangeStatusRepository changeStatusRepository;
	
    @Autowired
    protected JwtTokenUtil jwtTokenUtil;
    
	@Autowired
	private UserService userService;

	@Override
	public  ResponseEntity<Response<TicketEntity>> createTicket(TicketEntity ticket, BindingResult result, HttpServletRequest request) {
		Response<TicketEntity> response = new Response<TicketEntity>();
		
		try {
			validateCreateTicket(ticket, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(StatusEnum.getStatus("New"));
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			TicketEntity ticketPersisted = ticketRepository.save(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@Override
	public  ResponseEntity<Response<TicketEntity>> updateTicket(TicketEntity ticket, BindingResult result, HttpServletRequest request) {
		Response<TicketEntity> response = new Response<TicketEntity>();
		try {
			validateUpdateTicket(ticket, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			TicketEntity ticketCurrent = ticketRepository.findOne(ticket.getId());
			if(ticketCurrent == null) {
				result.addError(new ObjectError("TicketEntity", "Ticket não encontrado"));
				return ResponseEntity.badRequest().body(response);
			}
			
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setUser(ticketCurrent.getUser());
			ticket.setDate(ticketCurrent.getDate());
			ticket.setNumber(ticketCurrent.getNumber());
			
			if(ticketCurrent.getAssignedUser() != null) {
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			}
			TicketEntity ticketPersisted = ticketRepository.save(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateCreateTicket(TicketEntity ticket, BindingResult result) {
		if (ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Titulo não informado"));
			return;
		}
	}
	
	private void validateUpdateTicket(TicketEntity ticket, BindingResult result) {
		if (ticket.getId() == null) {
			result.addError(new ObjectError("Ticket", "Id não informado"));
			return;
		}
		if (ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Titulo não informado"));
			return;
		}
	}
	
	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}
	
	public UserEntity userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUserNameFromToken(token);
        return userService.findByEmail(email);
    }

	public ResponseEntity<Response<TicketEntity>> findById(String id) {
		Response<TicketEntity> response = new Response<TicketEntity>();
		TicketEntity ticket = ticketRepository.findOne(id);
		if (ticket == null) {
			response.getErrors().add("Register not found id:" + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		// PEGA AS ALTERACOES DO TICKET
		List<ChangeStatusEntity> changes = new ArrayList<ChangeStatusEntity>();
		Iterable<ChangeStatusEntity> changesCurrent = listChangeStatus(ticket.getId());
		for (Iterator<ChangeStatusEntity> iterator = changesCurrent.iterator(); iterator.hasNext();) {
			ChangeStatusEntity changeStatus = iterator.next();
			changeStatus.setTicket(null);
			changes.add(changeStatus);
		}	
		
		ticket.setChanges(changes);
		response.setData(ticket);
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<Response<String>> delete(String id) {
		Response<String> response = new Response<String>();
		
		TicketEntity ticket = ticketRepository.findOne(id);
		if (ticket == null) {
			response.getErrors().add("Ticket não encontrado para o id =" + id);
			return ResponseEntity.badRequest().body(response);
		}
		this.ticketRepository.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}

	public ResponseEntity<Response<Page<TicketEntity>>> listTicket(int page, int count, HttpServletRequest request) {
		Response<Page<TicketEntity>> response = new Response<Page<TicketEntity>>();
		Pageable pages = new PageRequest(page, count);
		
		Page<TicketEntity> tickets = null;
		
		UserEntity userRequest = userFromRequest(request);
		if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
			tickets = ticketRepository.findAll(pages);
		} else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
			tickets = findByCurrentUser(page, count, userRequest.getId());
		}
		response.setData(tickets);
		return ResponseEntity.ok(response);
	}

	public Iterable<TicketEntity> findAll() {
		return this.ticketRepository.findAll();
	}

	public Page<TicketEntity> findByCurrentUser(int page, int count, String userId) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByUserIdOrderByDateDesc(pages, userId);
	}

	public ResponseEntity<Response<TicketEntity>> createChangeStatus(String id, String status, 
			BindingResult result, HttpServletRequest request) {
		Response<TicketEntity> response = new Response<TicketEntity>();
		try {
			
			// VALIDA A MUDANCA DE STATUS
			validateChangeStatus(id, status, result);
			
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				
				return ResponseEntity.badRequest().body(response);
			}
			
			TicketEntity ticketCurrent = ticketRepository.findOne(id);
			
			ticketCurrent.setStatus(StatusEnum.getStatus(status));
			if(status.equals("Assigned")) {
				ticketCurrent.setAssignedUser(userFromRequest(request));
			}
			
			TicketEntity ticketPersisted = ticketRepository.save(ticketCurrent);
			ChangeStatusEntity changeStatus = new ChangeStatusEntity();
			changeStatus.setUserChange(userFromRequest(request));
			changeStatus.setDateChangeStatus(new Date());
			changeStatus.setStatus(StatusEnum.getStatus(status));
			changeStatus.setTicket(ticketPersisted);
			
			changeStatusRepository.save(changeStatus);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateChangeStatus(String id,String status, BindingResult result) {
		if (id == null || id.equals("")) {
			result.addError(new ObjectError("TicketEntity", "Id não Informado"));
			return;
		}
		if (status == null || status.equals("")) {
			result.addError(new ObjectError("TicketEntity", "Status não Informado"));
			return;
		}
	}

	public Iterable<ChangeStatusEntity> listChangeStatus(String ticketId) {
		return this.changeStatusRepository.findByTicketIdOrderByDateChangeStatus(ticketId);
	}

	public Page<TicketEntity> findByParameters(int page, int count, String title, String status, String priority) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status,
				priority, pages);
	}

	public Page<TicketEntity> findByParametersAndCurrentUser(int page, int count, String title, String status,
			String priority, String userId) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title,
				status, priority, userId, pages);
	}

	public Page<TicketEntity> findByNumber(int page, int count, Integer number) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByNumber(number, pages);
	}

	public Page<TicketEntity> findByParametersAndAssignedUser(int page, int count, String title, String status,
			String priority, String assignedUserId) {
		Pageable pages = new PageRequest(page, count);
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserOrderByDateDesc(
				title, status, priority, assignedUserId, pages);
	}

}
