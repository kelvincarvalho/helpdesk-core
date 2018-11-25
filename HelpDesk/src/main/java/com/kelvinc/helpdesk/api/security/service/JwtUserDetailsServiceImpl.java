package com.kelvinc.helpdesk.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.security.jwt.JwtUserFactory;
import com.kelvinc.helpdesk.api.service.UserService;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity user = userService.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("Não tem usuario para o email '%s'.", email));
		} else {
			return JwtUserFactory.create(user);
		}
	}

}
