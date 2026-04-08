package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;

public class TestUserData {

    public static UserEntity createBaseTestUserEntity(String suffix) {
        return UserEntity.builder()
                .username("user_" + suffix)
                .email("user_" + suffix + "@test.com")
                .password("password")
                .build();
    }

    public static UserEntity createTestUserEntity(String username, String email) {
        return UserEntity.builder()
                .username(username)
                .email(email)
                .password("password")
                .build();
    }

    // Entities ----------------------------------------------------------
    public static UserEntity createTestUserEntityA(){
        return UserEntity.builder()
                                .username("userA")
                                .password("encryptedtestpassw1")
                                .email("userA@testmail.com")
                                .build();
    }
    
    public static UserEntity createTestUserEntityB(){
        return UserEntity.builder()
                                .username("userB")
                                .password("encryptedtestpassw2")
                                .email("userB@testmail.com")
                                .build();
    }

    public static UserEntity createTestUserEntityC(){
        return UserEntity.builder()
                                .username("userC")
                                .password("encryptedtestpassw3")
                                .email("userC@testmail.com")
                                .build();
    }

    // UPDATE DTO -------------------------------------------------------------
    public static UpdateUserRequestDto createTestUpdateUserRequestDtoA(){
        return UpdateUserRequestDto.builder()
                                        .username(null)
                                        .password(null)
                                        .email(null)
                                        .build();
    }

    

}
