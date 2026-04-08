package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskBoardControllerIntegrationTest extends BaseAuthenticatedIntegrationTest {

    // Repositories to save test data to the database:
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskGroupRepository taskGroupRepository;
    @Autowired private TaskBoardRepository taskBoardRepository;

    @Test
    public void shouldReturn401_whenRequestWithoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/taskboards"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // CREATE -----------------------------------------------------------

    @Test
    public void shouldCreateTaskBoard_whenValidRequest() throws Exception {
        CreateTaskBoardRequestDto testTaskBoardDtoA =
                TestTaskBoardData.createTestTaskBoardRequestDtoA();

        String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.post("/taskboards"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskBoardJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void shouldReturnCreatedTaskBoard_whenValidRequest() throws Exception {
        CreateTaskBoardRequestDto testTaskBoardDtoA =
                TestTaskBoardData.createTestTaskBoardRequestDtoA();

        String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.post("/taskboards"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(taskBoardJson))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskBoardName").value("Task Board A"));
    }

    @Test
    public void shouldReturn400_whenCreatingTaskBoardWithInvalidData() throws Exception {
        CreateTaskBoardRequestDto dto = new CreateTaskBoardRequestDto();
        dto.setTaskBoardName(""); // invalid

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.post("/taskboards"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void shouldDeleteTaskBoard_whenOwnedByUser() throws Exception {
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.saveAndFlush(testTaskBoardEntityA);

        assertThat(
                taskBoardRepository
                        .findById(testTaskBoardEntityA.getId())
                        .equals(testTaskBoardEntityA));

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/" + testTaskBoardEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(taskBoardRepository.findById(testTaskBoardEntityA.getId()).isEmpty());
    }

    @Test
    public void shouldDeleteOnlySpecifiedTaskBoard() throws Exception {
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.saveAndFlush(testTaskBoardEntityA);

        TaskBoardEntity testTaskBoardEntityB = TestTaskBoardData.createTestTaskBoardEntityB(user);
        taskBoardRepository.saveAndFlush(testTaskBoardEntityB);

        assertThat(taskBoardRepository.findAll().size()).isEqualTo(2);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/" + testTaskBoardEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<TaskBoardEntity> result = taskBoardRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskBoardEntity::getId)
                .containsExactly(testTaskBoardEntityB.getId());
    }

    @Test
    public void shouldCascadeDeleteGroupsAndTasks() throws Exception {
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.saveAndFlush(testTaskBoardEntityA);

        TaskGroupEntity testTaskGroupEntityA =
                TestTaskGroupData.createTaskGroupEntityA(testTaskBoardEntityA);
        taskGroupRepository.saveAndFlush(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.saveAndFlush(testTaskEntityA);

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(1);
        assertThat(taskRepository.findAll().size()).isEqualTo(1);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.delete(
                                                "/taskboards/" + testTaskBoardEntityA.getId()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository.findAll()).isEmpty();
        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldReturn404_whenDeletingOtherUsersBoard() throws Exception {
        UserEntity otherUser = userRepository.saveAndFlush(TestUserData.createTestUserEntityB());

        TaskBoardEntity board =
                taskBoardRepository.saveAndFlush(
                        TestTaskBoardData.createTestTaskBoardEntityA(otherUser));

        mockMvc.perform(
                        authenticated(
                                MockMvcRequestBuilders.delete("/taskboards/" + board.getId())))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn404_whenDeletingNonExistingBoard() throws Exception {
        mockMvc.perform(authenticated(MockMvcRequestBuilders.delete("/taskboards/999")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // READ -----------------------------------------------------------

    @Test
    public void shouldReturnTaskBoards_whenUserHasBoards() throws Exception {
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.saveAndFlush(testTaskBoardA);

        mockMvc.perform(authenticated(MockMvcRequestBuilders.get("/taskboards")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].taskBoardName").value("Task Board A"));
    }

    @Test
    public void shouldReturnEmptyList_whenUserHasNoBoards() throws Exception {
        mockMvc.perform(authenticated(MockMvcRequestBuilders.get("/taskboards")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    // UPDATE -----------------------------------------------------------

    @Test
    public void shouldUpdateTaskBoard_whenValidRequest() throws Exception {
        TaskBoardEntity testTaskBoardA =
                taskBoardRepository.saveAndFlush(
                        TestTaskBoardData.createTestTaskBoardEntityA(user));

        CreateTaskBoardRequestDto dto = TestTaskBoardData.createTestTaskBoardRequestDtoA();
        dto.setTaskBoardName("UPDATED");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(
                                        MockMvcRequestBuilders.patch(
                                                "/taskboards/" + testTaskBoardA.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskBoardName").value("UPDATED"));
    }

    @Test
    public void shouldReturn404_whenUpdatingNonExistingBoard() throws Exception {
        CreateTaskBoardRequestDto dto = TestTaskBoardData.createTestTaskBoardRequestDtoA();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        authenticated(MockMvcRequestBuilders.patch("/taskboards/999"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
