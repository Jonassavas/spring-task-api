package com.jonassavas.spring_task_api.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserResponseDto;
import com.jonassavas.spring_task_api.domain.dto.user.UserDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.exceptions.ConflictException;
import com.jonassavas.spring_task_api.mappers.Mapper;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.security.JwtService;
import com.jonassavas.spring_task_api.security.SecurityService;
import com.jonassavas.spring_task_api.services.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            SecurityService securityService,
            PasswordEncoder passwordEncoder,
            Mapper<UserEntity, UserDto> userMapper,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }
    
    @Override
    public UserDto getCurrentUser() {
        UserEntity user = securityService.getCurrentUser();
        return userMapper.mapTo(user);
    }

    @Override
    public UpdateUserResponseDto updateCurrentUser(UpdateUserRequestDto dto) {

        UserEntity user = securityService.getCurrentUser();

        boolean usernameChanged = false;
        boolean passwordChanged = false;
        boolean updated = false;

        // Update username
        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && !dto.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new ConflictException("Username already taken");
            }

            user.setUsername(dto.getUsername());
            usernameChanged = true;
            updated = true;
        }

        // Update email
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Email already taken");
            }

            user.setEmail(dto.getEmail());
            updated = true;
        }

        // Update password
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            passwordChanged = true;
            updated = true;
        }

        // Only save if something changed
        UserEntity savedUser = user;
        if (updated) {
            savedUser = userRepository.save(user);
        }

        // Only generate new token if username or password changed
        String newToken = null;
        if (usernameChanged || passwordChanged) {
            newToken = jwtService.generateToken(savedUser.getUsername());
        }

        UserDto userDto = userMapper.mapTo(savedUser);

        return UpdateUserResponseDto.builder()
                .user(userDto)
                .token(newToken)
                .expiresIn(newToken != null ? jwtService.getExpirationMs() : null)
                .build();
    } 

    @Override
    public void deleteCurrentUser() {
        UserEntity user = securityService.getCurrentUser();
        userRepository.delete(user);
    }
}