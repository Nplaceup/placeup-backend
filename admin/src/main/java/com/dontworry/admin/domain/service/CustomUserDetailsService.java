package com.dontworry.admin.domain.service;

import com.dontworry.admin.security.CustomUserDetails;
import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.admin.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isAdmin()) {
            throw new UsernameNotFoundException("Not admin user");
        }

        return new CustomUserDetails(user);
    }
}
