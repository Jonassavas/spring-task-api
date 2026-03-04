package com.jonassavas.spring_task_api.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.UserRepository;

// This class has a single responsibility:
//  - Load 'UserEntity' for Spring Security authentification.
// Works as an adapter between Spring Security and the database.

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException{
        UserEntity user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(
                            "Username not found: " + username));

        return org.springframework.security.core.userdetails.User
                            .builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .roles("USER")
                            .build();
    }
}
