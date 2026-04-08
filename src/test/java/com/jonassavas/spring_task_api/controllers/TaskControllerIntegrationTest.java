package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
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




@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest extends BaseControllerIntegrationTest {
    
    // Repositories to save test data to the database:
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskGroupRepository taskGroupRepository;
    @Autowired private TaskBoardRepository taskBoardRepository;

    /* Prerequisit data:
        A Task needs a taskGroup, which in turn requires a taskBoard.
            - TaskBoard --> TaskGroup --> Task 
    */  
    private TaskBoardEntity taskBoard; 
    private TaskGroupEntity taskGroupA;
    private TaskGroupEntity taskGroupB;

    
    @BeforeEach
    public void setUp() {
        // Clear previous test data in reverse FK order
        taskRepository.deleteAll();
        taskGroupRepository.deleteAll();
        taskBoardRepository.deleteAll();

        // Create a task board for the current user
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA(user)
        );

        // Create two task groups for this board
        taskGroupA = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
        taskGroupB = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityB(taskBoard)
        );
    } 

    // CREATE -----------------------------------------------------------

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Task A"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupId").value(taskGroupA.getId())); 
    }

    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupId").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect( 
            MockMvcResultMatchers.jsonPath("$.taskName").value("Task A")
        );

        assertThat(taskRepository.findAll())
            .extracting(TaskEntity::getTaskName)
            .contains("Task A");
    }
    
    @Test
    public void testThatCreateTaskWithoutValidTaskGroupReturns404() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.post("/groups/" + 99 + "/tasks"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatCreateTaskWithoutTokenReturns401() throws Exception {
        String taskJson = objectMapper.writeValueAsString(
            TestTaskData.createTestTaskRequestDtoA(taskGroupA)
        );

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testCreateTaskWithInvalidFieldsReturns400() throws Exception {
        CreateTaskRequestDto dto = TestTaskData.createTestTaskRequestDtoA(taskGroupA);
        dto.setTaskName(""); // Invalid

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteTaskReturnsHttp204() throws Exception{
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        assertThat(taskGroupRepository
                    .findByIdWithTasksAndUsername(taskGroupA.getId(), user.getUsername()).get()
                        .getTasks().size())
                .isEqualTo(1); 

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId())) 
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository
                    .findByIdWithTasksAndUsername(taskGroupA.getId(), user.getUsername()).get()
                        .getTasks().size())
                .isEqualTo(0); 
    }

    @Test
    public void testThatDeleteTaskDeletesCorrectTask() throws Exception{
        TaskEntity testTaskEntityA = 
            taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        TaskEntity testTaskEntityB = 
            taskRepository.save(TestTaskData.createTestTaskEntityB(taskGroupA));

        assertThat(taskGroupRepository
                    .findByIdWithTasksAndUsername(taskGroupA.getId(), user.getUsername()).get()
                        .getTasks().size())
                .isEqualTo(2); 

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId()))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());
        
        List<TaskEntity> result = taskGroupRepository
                                    .findByIdWithTasksAndUsername(taskGroupA.getId(), user.getUsername())
                                    .get()
                                    .getTasks();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityB.getId());
        
    }

    @Test
    public void testThatDeleteNonExistentTaskReturns404() throws Exception {
        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.delete("/tasks/9999"))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // UPDATE -----------------------------------------------------------

    @Test
    public void testUpdateTaskName() throws Exception{
        TaskEntity testTaskEntityA = 
            taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        UpdateTaskRequestDto testRequestTaskDto =
            TestTaskData.updateTestRequestTaskDto(taskGroupA);
        testRequestTaskDto.setTaskName("UPDATED");

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskName").value("UPDATED")
        );
    }

    @Test
    public void testUpdateTaskGroupId() throws Exception{
        // Creating tasks for taskGroup: A
        TaskEntity testTaskEntityA = 
            taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        // Creating tasks for another taskGroup: B
        UpdateTaskRequestDto testRequestTaskDto = 
            TestTaskData.updateTestRequestTaskDto(taskGroupB);
        testRequestTaskDto.setTaskGroupId(taskGroupB.getId());

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupId").value(taskGroupB.getId())
        );
    }

    @Test
    public void testUpdateBothTaskGroupIdAndTaskName() throws Exception{

        // Creating task for taskGroup: A
        TaskEntity testTaskEntityA = 
            taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        // Creating request to change testTaskEntityA from taskGroup: A --> B
        UpdateTaskRequestDto testRequestTaskDto = TestTaskData.updateTestRequestTaskDto(taskGroupB);
        testRequestTaskDto.setTaskGroupId(taskGroupB.getId());
        testRequestTaskDto.setTaskName("UPDATED");

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            authenticated(MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupId").value(taskGroupB.getId())
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskName").value("UPDATED")
        );
    }

    @Test
    public void testUpdateNonExistentTaskReturns404() throws Exception {
        UpdateTaskRequestDto dto = TestTaskData.updateTestRequestTaskDto(taskGroupA);
        dto.setTaskName("NEW_NAME");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/tasks/9999"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateTaskWithInvalidNameReturns400() throws Exception {
        TaskEntity task = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        UpdateTaskRequestDto dto = TestTaskData.updateTestRequestTaskDto(taskGroupA);
        dto.setTaskName("");  // Invalid empty name

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/tasks/" + task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUpdateTaskOfAnotherUserReturns404() throws Exception {
        // Create a different user
        UserEntity otherUser = userRepository.saveAndFlush(TestUserData.createBaseTestUserEntity("otherUser"));
        TaskBoardEntity otherBoard = taskBoardRepository.save(TestTaskBoardData.createTestTaskBoardEntityA(otherUser));
        TaskGroupEntity otherGroup = taskGroupRepository.save(TestTaskGroupData.createTaskGroupEntityA(otherBoard));
        TaskEntity otherTask = taskRepository.save(TestTaskData.createTestTaskEntityA(otherGroup));

        UpdateTaskRequestDto dto = TestTaskData.updateTestRequestTaskDto(otherGroup);
        dto.setTaskName("NEW_NAME");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/tasks/" + otherTask.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUpdateTaskWithNoChangesReturns200() throws Exception {
        TaskEntity task = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        UpdateTaskRequestDto dto = new UpdateTaskRequestDto(); // No changes

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/tasks/" + task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value(task.getTaskName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupId").value(task.getTaskGroup().getId()));
    }

    @Test
    public void testUpdateTaskGroupIdToSameValueReturns200() throws Exception {
        TaskEntity task = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        UpdateTaskRequestDto dto = TestTaskData.updateTestRequestTaskDto(taskGroupA);
        dto.setTaskGroupId(taskGroupA.getId());

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                authenticated(MockMvcRequestBuilders.patch("/tasks/" + task.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.taskGroupId").value(taskGroupA.getId()));
    }

}
