package com.jonassavas.spring_task_api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.domain.dto.task.CreateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task.UpdateTaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.security.JwtService;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;
import com.jonassavas.util.TestUserData;




@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskControllerIntegrationTests {
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Repositories to save test data to the database:
    private TaskRepository taskRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskBoardRepository taskBoardRepository;
    private UserRepository userRepository;

    private JwtService jwtService;
    private String token;

    /* Prerequisit data:
        A Task needs a taskGroup, which in turn requires a taskBoard.
            - TaskBoard --> TaskGroup --> Task 
    */  
    private UserEntity user;
    private TaskBoardEntity taskBoard; 
    private TaskGroupEntity taskGroupA;
    private TaskGroupEntity taskGroupB;

    @Autowired
    public TaskControllerIntegrationTests(
            MockMvc mockMvc, 
            ObjectMapper objectMapper,
            TaskRepository taskRepository,
            TaskGroupRepository taskGroupRepository,
            TaskBoardRepository taskBoardRepository,
            UserRepository userRepository,
            JwtService jwtService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskBoardRepository = taskBoardRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    } 
    
    @BeforeEach
    public void setUp(){
        user = userRepository.save(
            TestUserData.createTestUserEntityA()
        );

        token = jwtService.generateToken(user.getUsername());

        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA(user)
        );
        taskGroupA = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
        taskGroupB = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
    }

    // CREATE -----------------------------------------------------------

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupId").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect( 
            MockMvcResultMatchers.jsonPath("$.taskName").value("Task A")
        );
    }
    
    @Test
    public void testThatCreateTaskWithoutValidTaskGroupReturns404() throws Exception{
        CreateTaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + 99 + "/tasks")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
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
            MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId())
            .header("Authorization", "Bearer " + token)
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
            MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId())
            .header("Authorization", "Bearer " + token)
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
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
            .header("Authorization", "Bearer " + token)
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
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
            .header("Authorization", "Bearer " + token)
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
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
            .header("Authorization", "Bearer " + token)
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
}
