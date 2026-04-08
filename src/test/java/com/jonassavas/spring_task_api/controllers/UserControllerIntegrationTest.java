package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.jonassavas.spring_task_api.domain.dto.user.UpdateUserRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;
import com.jonassavas.util.TestUserData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseAuthenticatedIntegrationTest {

    // Repositories to verify cascade deletion
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskGroupRepository taskGroupRepository;
    @Autowired private TaskBoardRepository taskBoardRepository;

    // READ ------------------------------------------------------

    @Test
    public void testThatGetCurrentUserReturnsCorrectDtoStructure() throws Exception {
        mockMvc.perform(authenticated(MockMvcRequestBuilders.get("/users/me")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void testThatGetCurrentUserReturnsUser() throws Exception {
        mockMvc.perform(authenticated(MockMvcRequestBuilders.get("/users/me")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void testThatGetCurrentUserFailsWithoutToken() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // UPDATE -----------------------------------------------------

    @Test
    public void testThatUpdateUserReturns200() throws Exception {
        UpdateUserRequestDto dto =
                UpdateUserRequestDto.builder().email("updated0@email.com").build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.patch("/users/me"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatUpdateUserActuallyUpdatesData() throws Exception {
        UpdateUserRequestDto dto =
                UpdateUserRequestDto.builder().email("updated@email.com").build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.patch("/users/me"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.email").value("updated@email.com"));

        // Verify DB state
        UserEntity updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    public void testThatUpdateUserFailsWithInvalidEmail() throws Exception {
        UpdateUserRequestDto dto = UpdateUserRequestDto.builder().email("invalid-email").build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.patch("/users/me"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteUserReturns204() throws Exception {
        mockMvc.perform(authenticated(MockMvcRequestBuilders.delete("/users/me")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatDeleteUserRemovesUserFromDatabase() throws Exception {
        Long userId = user.getId();

        mockMvc.perform(authenticated(MockMvcRequestBuilders.delete("/users/me")))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    public void testThatRequestWithoutTokenReturns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testThatDeletingUserAlsoDeletesAssociatedTaskData() throws Exception {
        // Create a user with a task board, group, and tasks
        UserEntity testUser =
                userRepository.saveAndFlush(TestUserData.createBaseTestUserEntity("cascadeUser"));
        String userToken = jwtService.generateToken(testUser.getUsername());

        TaskBoardEntity board =
                taskBoardRepository.save(TestTaskBoardData.createTestTaskBoardEntityA(testUser));
        TaskGroupEntity group =
                taskGroupRepository.save(TestTaskGroupData.createTaskGroupEntityA(board));
        TaskEntity task1 = taskRepository.save(TestTaskData.createTestTaskEntityA(group));
        TaskEntity task2 = taskRepository.save(TestTaskData.createTestTaskEntityB(group));

        // Verify all entities exist
        assertThat(userRepository.findById(testUser.getId())).isPresent();
        assertThat(taskBoardRepository.findById(board.getId())).isPresent();
        assertThat(taskGroupRepository.findById(group.getId())).isPresent();
        assertThat(taskRepository.findAll())
                .extracting(TaskEntity::getId)
                .containsExactlyInAnyOrder(task1.getId(), task2.getId());

        // Delete user
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/me")
                                .header("Authorization", "Bearer " + userToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // All associated entities should be removed
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
        assertThat(taskBoardRepository.findAll()).isEmpty();
        assertThat(taskGroupRepository.findAll()).isEmpty();
        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    public void testThatDeleteUserCascadesAndBlocksAccess() throws Exception {
        // Setup: create a user with a task board, group, and tasks
        UserEntity cascadeUser =
                userRepository.saveAndFlush(
                        TestUserData.createBaseTestUserEntity("user_readCascadeUser"));
        String cascadeToken = jwtService.generateToken(cascadeUser.getUsername());

        TaskBoardEntity board =
                taskBoardRepository.save(TestTaskBoardData.createTestTaskBoardEntityA(cascadeUser));

        TaskGroupEntity group =
                taskGroupRepository.save(TestTaskGroupData.createTaskGroupEntityA(board));

        TaskEntity taskA = taskRepository.save(TestTaskData.createTestTaskEntityA(group));
        TaskEntity taskB = taskRepository.save(TestTaskData.createTestTaskEntityB(group));

        // Sanity check: everything exists
        assertThat(userRepository.findById(cascadeUser.getId())).isPresent();
        assertThat(taskBoardRepository.findAll()).isNotEmpty();
        assertThat(taskGroupRepository.findAll()).isNotEmpty();
        assertThat(taskRepository.findAll()).isNotEmpty();

        // Perform DELETE request as this user
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/me")
                                .header("Authorization", "Bearer " + cascadeToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // DB Verification: cascade deletion
        assertThat(userRepository.findById(cascadeUser.getId())).isEmpty();
        assertThat(taskBoardRepository.findAll()).isEmpty();
        assertThat(taskGroupRepository.findAll()).isEmpty();
        assertThat(taskRepository.findAll()).isEmpty();

        // Verify old token no longer works — expect 401 because user no longer exists
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/me")
                                .header("Authorization", "Bearer " + cascadeToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
