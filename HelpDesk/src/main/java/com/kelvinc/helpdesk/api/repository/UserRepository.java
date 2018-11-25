package com.kelvinc.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kelvinc.helpdesk.api.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {

	UserEntity findByEmail(String email);
	
}
