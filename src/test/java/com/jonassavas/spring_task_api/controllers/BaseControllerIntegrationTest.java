package com.jonassavas.spring_task_api.controllers;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.security.JwtService;
import com.jonassavas.util.TestUserData;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected JwtService jwtService;
    @Autowired protected UserRepository userRepository;

    protected String token;
    protected UserEntity user;

    @BeforeEach
    void baseSetUp() {
        user = createAndSaveUser();
        token = jwtService.generateToken(user.getUsername());
    }


    /*
            - DB has a limit of 30 chars for usernames.
            - Users persist through controller tests.
            - UUID of 20 chars could potentially collide
              and cause test failures.
            - If this happens:
                - Read the output
                - Rerun the tests
                - Buy a lottery ticket (~1.2trillion combinations)
        */
    protected UserEntity createAndSaveUser() {
        String unique = UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 20); 

        return userRepository.saveAndFlush(
                TestUserData.createBaseTestUserEntity(unique)
        );
    }

    protected MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }
}