package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseControllerIntegrationTest {
    
    // READ ------------------------------------------------------

    @Test
    public void testThatGetCurrentUserReturnsCorrectDtoStructure() throws Exception {
        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.get("/users/me"))
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }
    
    @Test
    public void testThatGetCurrentUserReturnsUser() throws Exception {
        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.get("/users/me"))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.username")
                        .value(user.getUsername())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email")
                        .value(user.getEmail())
        );
    }

    @Test
    public void testThatGetCurrentUserFailsWithoutToken() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        );
    }



    // UPDATE -----------------------------------------------------


    @Test
    public void testThatUpdateUserReturns200() throws Exception {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder()
                .email("updated0@email.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/users/me"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUpdateUserActuallyUpdatesData() throws Exception {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder()
                .email("updated@email.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/users/me"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.email")
                        .value("updated@email.com")
        );

        // Verify DB state
        UserEntity updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    public void testThatUpdateUserFailsWithInvalidEmail() throws Exception {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder()
                .email("invalid-email")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/users/me"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteUserReturns204() throws Exception {
        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.delete("/users/me"))
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testThatDeleteUserRemovesUserFromDatabase() throws Exception {
        Long userId = user.getId();

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.delete("/users/me"))
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    public void testThatRequestWithoutTokenReturns401() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/me")
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        );
    }
}
