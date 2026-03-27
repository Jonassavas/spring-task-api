package com.jonassavas.spring_task_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserResponseDto;
import com.jonassavas.spring_task_api.domain.dto.user.UserDto;
import com.jonassavas.spring_task_api.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserResponseDto> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequestDto dto) {

        return ResponseEntity.ok(userService.updateCurrentUser(dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}