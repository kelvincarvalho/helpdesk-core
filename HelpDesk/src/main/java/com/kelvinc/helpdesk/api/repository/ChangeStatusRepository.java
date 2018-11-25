package com.kelvinc.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kelvinc.helpdesk.api.entity.ChangeStatusEntity;

public interface ChangeStatusRepository extends MongoRepository<ChangeStatusEntity, String> {

	Iterable<ChangeStatusEntity> findByTicketIdOrderByDateChangeStatus(String idTicket);
	
}
