package com.kelvinc.helpdesk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kelvinc.helpdesk.api.entity.UserEntity;
import com.kelvinc.helpdesk.api.enums.ProfileEnum;
import com.kelvinc.helpdesk.api.repository.UserRepository;

@SpringBootApplication
public class HelpDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelpDeskApplication.class, args);
	}
	
	// AO INICIAR JÁ ESTOU CRIANDO UM USUÁRIO
	
    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            initUsers(userRepository, passwordEncoder);
        };

    }
	
	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@helpdesk.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setProfile(ProfileEnum.ROLE_ADMIN);

        // BUSCO SE JÁ ESTA CADASTRADO
        UserEntity find = userRepository.findByEmail("admin@helpdesk.com");
        if (find == null) {
            userRepository.save(admin);
        }
    }
}
