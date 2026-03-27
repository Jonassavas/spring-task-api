package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserResponseDto;
import com.jonassavas.spring_task_api.domain.dto.user.UserDto;

public interface UserService {
    UserDto getCurrentUser();
    public UpdateUserResponseDto updateCurrentUser(UpdateUserRequestDto dto);
    void deleteCurrentUser();
}
