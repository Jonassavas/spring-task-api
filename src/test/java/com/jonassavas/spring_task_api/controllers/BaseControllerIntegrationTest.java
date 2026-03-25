package com.jonassavas.spring_task_api.controllers;

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
        user = userRepository.save(TestUserData.createTestUserEntityA());
        token = jwtService.generateToken(user.getUsername());
    }

    protected MockHttpServletRequestBuilder authenticated(
            MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }
}