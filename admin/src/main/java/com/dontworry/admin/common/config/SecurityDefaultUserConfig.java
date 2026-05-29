package com.dontworry.admin.common.config;

import com.dontworry.admin.domain.repository.UsersRepository;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.user.enums.ActiveStatus;
import com.dontworry.core.domain.user.enums.PlanTier;
import com.dontworry.core.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityDefaultUserConfig {

    @Bean
    public CommandLineRunner initAdmin(UsersRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin").isPresent()) {
                System.out.println("Default admin already exists.");
                return;
            }

            repo.save(
                    Users.builder()
                            .userName("admin")
                            .email("admin")
                            .password(encoder.encode("admin"))
                            .role(UserRole.ADMIN)
                            .isActive(ActiveStatus.ACTIVE)
                            .build()
            );

            System.out.println("Default admin account created.");
        };
    }

}

