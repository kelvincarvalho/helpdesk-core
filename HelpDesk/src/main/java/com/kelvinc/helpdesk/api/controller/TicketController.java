package com.kelvinc.helpdesk.api.controller;

import java.util.Iterator;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kelvinc.helpdesk.api.dto.Summary;
import com.kelvinc.helpdesk.api.entity.TicketEntity;
import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.enums.ProfileEnum;
import com.kelvinc.helpdesk.api.enums.StatusEnum;
import com.kelvinc.helpdesk.api.response.Response;
import com.kelvinc.helpdesk.api.security.jwt.JwtTokenUtil;
import com.kelvinc.helpdesk.api.service.TicketService;
import com.kelvinc.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {
	

	@Autowired
	private TicketService ticketService;
	
    @Autowired
    protected JwtTokenUtil jwtTokenUtil;
    
	@Autowired
	private UserService userService;
	
	@PostMapping()
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<TicketEntity>> create(HttpServletRequest request, @RequestBody TicketEntity ticket,
			BindingResult result) {
		return ticketService.createTicket(ticket, result, request);
	}
	
	public UserEntity userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUserNameFromToken(token);
        return userService.findByEmail(email);
    }
	
	@PutMapping()
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<TicketEntity>> update(HttpServletRequest request, @RequestBody TicketEntity ticket,
			BindingResult result) {
		return ticketService.updateTicket(ticket, result, request);
	}
	
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<TicketEntity>> findById(@PathVariable("id") String id) {
		return ticketService.findById(id);
	}
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
		return ticketService.delete(id);
	}
	
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public  ResponseEntity<Response<Page<TicketEntity>>> findAll(HttpServletRequest request, @PathVariable int page, @PathVariable int count) {
		return ticketService.listTicket(page, count, request);
    }
	
	@GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public  ResponseEntity<Response<Page<TicketEntity>>> findByParams(HttpServletRequest request, 
    		 							@PathVariable int page, 
    		 							@PathVariable int count,
    		 							@PathVariable Integer number,
    		 							@PathVariable String title,
    		 							@PathVariable String status,
    		 							@PathVariable String priority,
    		 							@PathVariable boolean assigned) {
		
		title = title.equals("uninformed") ? "" : title;
		status = status.equals("uninformed") ? "" : status;
		priority = priority.equals("uninformed") ? "" : priority;
		
		Response<Page<TicketEntity>> response = new Response<Page<TicketEntity>>();
		Page<TicketEntity> tickets = null;
		if(number > 0) {
			tickets = ticketService.findByNumber(page, count, number);
		} else {
			UserEntity userRequest = userFromRequest(request);
			if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
				if(assigned) {
					tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
				} else {
					tickets = ticketService.findByParameters(page, count, title, status, priority);
				}
			} else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
				tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
			}
		}
		response.setData(tickets);
		return ResponseEntity.ok(response);
    }
	
	@PutMapping(value = "/{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<Response<TicketEntity>> changeStatus(
													@PathVariable("id") String id, 
													@PathVariable("status") String status, 
													HttpServletRequest request,  
													@RequestBody TicketEntity ticket,
													BindingResult result) {
		
		return ticketService.createChangeStatus(id, status, result, request);
	}
	

	
	@GetMapping(value = "/summary")
	public ResponseEntity<Response<Summary>> findChart() {
		Response<Summary> response = new Response<Summary>();
		Summary chart = new Summary();
		int amountNew = 0;
		int amountResolved = 0;
		int amountApproved = 0;
		int amountDisapproved = 0;
		int amountAssigned = 0;
		int amountClosed = 0;
		Iterable<TicketEntity> tickets = ticketService.findAll();
		if (tickets != null) {
			for (Iterator<TicketEntity> iterator = tickets.iterator(); iterator.hasNext();) {
				TicketEntity ticket = iterator.next();
				if(ticket.getStatus().equals(StatusEnum.New)){
					amountNew ++;
				}
				if(ticket.getStatus().equals(StatusEnum.Resolved)){
					amountResolved ++;
				}
				if(ticket.getStatus().equals(StatusEnum.Approved)){
					amountApproved ++;
				}
				if(ticket.getStatus().equals(StatusEnum.Disapproved)){
					amountDisapproved ++;
				}
				if(ticket.getStatus().equals(StatusEnum.Assigned)){
					amountAssigned ++;
				}
				if(ticket.getStatus().equals(StatusEnum.Closed)){
					amountClosed ++;
				}
			}	
		}
		chart.setAmountNew(amountNew);
		chart.setAmountResolved(amountResolved);
		chart.setAmountApproved(amountApproved);
		chart.setAmountDisapproved(amountDisapproved);
		chart.setAmountAssigned(amountAssigned);
		chart.setAmountClosed(amountClosed);
		response.setData(chart);
		return ResponseEntity.ok(response);
	}
	

}
