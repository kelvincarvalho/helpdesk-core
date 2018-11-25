package com.kelvinc.helpdesk.api.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.repository.UserRepository;
import com.kelvinc.helpdesk.api.response.Response;
import com.kelvinc.helpdesk.api.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserEntity findByEmail(String email) {
		return (UserEntity) this.userRepository.findByEmail(email);
	}

	@Override
	public ResponseEntity<Response<UserEntity>> createUser(UserEntity user, BindingResult result) {
		Response<UserEntity> response = new Response<UserEntity>();

		try {
			// VALIDA O USUARIO
			validateCreateUser(user, result);

			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));

				return ResponseEntity.badRequest().body(response);
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			UserEntity userPersisted = userRepository.save(user);
			response.setData(userPersisted);

		} catch (DuplicateKeyException dE) {
			response.getErrors().add("E-mail já registrado!");
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<Response<UserEntity>> updateUser(UserEntity user, BindingResult result) {
		Response<UserEntity> response = new Response<UserEntity>();

		try {
			// VALIDA O USUARIO
			validateUpdate(user, result);

			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			UserEntity userPersisted = userRepository.save(user);
			response.setData(userPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);

	}

	private void validateCreateUser(UserEntity user, BindingResult result) {
		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email não Informado"));
			return;
		}
	}

	private void validateUpdate(UserEntity user, BindingResult result) {

		if (user.getId() == null) {
			result.addError(new ObjectError("User", "Id não Informado"));
			return;
		}

		if (user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email não Informado"));
			return;
		}
	}

	@Override
	public ResponseEntity<Response<UserEntity>> findById(String id) {
		Response<UserEntity> response = new Response<UserEntity>();
		UserEntity user = userRepository.findOne(id);
		if (user == null) {
			response.getErrors().add("Nenhum usuário encontrado com o id = " + id);
			return ResponseEntity.badRequest().body(response);
		}
		response.setData(user);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Response<String>> delete(String id) {
		Response<String> response = new Response<String>();
		UserEntity user = userRepository.findOne(id);
		if (user == null) {
			response.getErrors().add("Registro não encontrado, id = " + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		this.userRepository.delete(id);
		return ResponseEntity.ok(new Response<String>());

	}

	@Override
	public ResponseEntity<Response<Page<UserEntity>>> findAll(int page, int count) {
		Response<Page<UserEntity>> response = new Response<Page<UserEntity>>();
		
		Pageable pages = new PageRequest(page, count);
		Page<UserEntity> users = userRepository.findAll(pages);
		response.setData(users);
		
		return ResponseEntity.ok(response);
	}
	
	
}
