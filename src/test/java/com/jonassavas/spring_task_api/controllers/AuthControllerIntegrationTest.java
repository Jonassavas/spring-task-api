package com.jonassavas.spring_task_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.util.TestAuthData;
import com.jonassavas.util.TestUserData;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest extends BaseControllerIntegrationTest {
    
    @Autowired private PasswordEncoder passwordEncoder;

    // REGISTER --------------------------------------------------

    @Test
    public void testRegisterReturns201Created() throws Exception {
        var request = TestAuthData.createTestRegisterRequestDto();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testRegisterReturnsToken() throws Exception {
        var request = TestAuthData.createTestRegisterRequestDto();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.token")
                        .isString()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.expiresIn")
                        .isNumber()
        );
    }

    @Test
    public void testRegisterFailsWhenUsernameExists() throws Exception {
        var request = TestAuthData.createTestRegisterRequestDto();

        // Save first user
        userRepository.saveAndFlush(
                TestUserData.createTestUserEntity(request.getUsername(), request.getEmail())
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testRegisterFailsWhenEmailExists() throws Exception {
        var request = TestAuthData.createTestRegisterRequestDto();

        userRepository.saveAndFlush(
                TestUserData.createTestUserEntity(
                        "differentUsername",
                        request.getEmail()
                )
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testRegisterFailsValidation() throws Exception {
        var request = TestAuthData.createTestRegisterRequestDto();
        request.setEmail("invalid-email");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    // LOGIN --------------------------------------------------------------

    @Test
    public void testLoginReturnsTokenWhenValid() throws Exception {
        UserEntity user = TestUserData.createTestUserEntityA();
        user.setPassword(passwordEncoder.encode("encryptedtestpasswd1"));

        userRepository.saveAndFlush(user); 

        var request = TestAuthData.createTestLoginRequestDto(
                user.getUsername(),
                "encryptedtestpasswd1"
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.token").isString()
        );
    }

    @Test
    public void testLoginFailsWithWrongPassword() throws Exception {
        UserEntity user = TestUserData.createTestUserEntityA();
        user.setPassword(passwordEncoder.encode("correctPassword"));

        userRepository.saveAndFlush(user);

        var request = TestAuthData.createTestLoginRequestDto(
                user.getUsername(),
                "wrongPassword"
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        );
    }

    @Test
    public void testLoginFailsWhenUserNotFound() throws Exception {
        var request = TestAuthData.createTestLoginRequestDto(
                "nonexistent",
                "password"
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        );
    }
}
