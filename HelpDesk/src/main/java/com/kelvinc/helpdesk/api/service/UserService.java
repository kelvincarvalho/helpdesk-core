package com.kelvinc.helpdesk.api.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.response.Response;

@Component
public interface UserService {

	UserEntity findByEmail(String email);
	
	ResponseEntity<Response<UserEntity>> createUser(UserEntity user, BindingResult result);
	
	ResponseEntity<Response<UserEntity>> updateUser(UserEntity user, BindingResult result);
	
	ResponseEntity<Response<UserEntity>> findById(String id);
	
	ResponseEntity<Response<String>> delete(String id);
	
	ResponseEntity<Response<Page<UserEntity>>> findAll(int page, int count);
}
