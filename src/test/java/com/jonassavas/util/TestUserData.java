package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.entities.UserEntity;

public class TestUserData {

    // Entities ----------------------------------------------------------
    public static UserEntity createTestUserEntityA(){
        return UserEntity.builder()
                                .username("userA")
                                .password("encryptedtestpassw1")
                                .email("userA@testmail.com")
                                .build();
    }
    
    public static UserEntity createTestUserEntityB(UserEntity user){
        return UserEntity.builder()
                                .username("userB")
                                .password("encryptedtestpassw2")
                                .email("userB@testmail.com")
                                .build();
    }

    public static UserEntity createTestUserEntityC(UserEntity user){
        return UserEntity.builder()
                                .username("userC")
                                .password("encryptedtestpassw3")
                                .email("userC@testmail.com")
                                .build();
    }

    // CREATE DTOs -------------------------------------------------------------


    // UPDATE DTOs -------------------------------------------------------------
}
