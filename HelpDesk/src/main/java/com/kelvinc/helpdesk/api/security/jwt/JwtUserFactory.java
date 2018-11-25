package com.kelvinc.helpdesk.api.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.enums.ProfileEnum;

public class JwtUserFactory {

	// FAZ A CONVERSAO DO USER

	private JwtUserFactory() {
	}

	//Gerar um usuário JWTUSER
	public static JwtUser create(UserEntity user) {
		return new JwtUser(user.getId(), user.getEmail(), user.getPassword(),
				mapToGrantedAuthorities(user.getProfile()));
	}
	
	// CONVERTE PERFIL DO USUARIO PARA O FORMATO DO SPRING SECURITY
	private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
		return authorities;
	}

}
