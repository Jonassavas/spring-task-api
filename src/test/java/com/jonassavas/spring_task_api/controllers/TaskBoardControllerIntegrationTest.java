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
import com.jonassavas.spring_task_api.domain.dto.task_board.CreateTaskBoardRequestDto;
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
public class TaskBoardControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // Repositories to save test data to the database:
    private TaskRepository taskRepository;
    private TaskGroupRepository taskGroupRepository; 
    private TaskBoardRepository taskBoardRepository;
    private UserRepository userRepository;

    private JwtService jwtService;

    private String token;
    private UserEntity user;


    @Autowired
    public TaskBoardControllerIntegrationTest(
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
    }

    // CREATE -----------------------------------------------------------

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
        CreateTaskBoardRequestDto testTaskBoardDtoA = 
            TestTaskBoardData.createTestTaskBoardRequestDtoA();

        String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

        mockMvc.perform(
        MockMvcRequestBuilders.post("/taskboards")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskBoardJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated() 
        );
    }
    
    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
       CreateTaskBoardRequestDto testTaskBoardDtoA = TestTaskBoardData.createTestTaskBoardRequestDtoA();

       String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

       mockMvc.perform(
        MockMvcRequestBuilders.post("/taskboards")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskBoardJson)
       ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber() 
       ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskBoardName").value("Task Board A") 
       );
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteTaskBoardReturnsHttp204() throws Exception{
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.save(testTaskBoardEntityA);

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .equals(testTaskBoardEntityA));

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + testTaskBoardEntityA.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .isEmpty());
    }

    @Test
    public void testThatDeleteTaskBoardDeletesCorrectBoard() throws Exception{
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.save(testTaskBoardEntityA);

        TaskBoardEntity testTaskBoardEntityB = TestTaskBoardData.createTestTaskBoardEntityB(user);
        taskBoardRepository.save(testTaskBoardEntityB);

        assertThat(taskBoardRepository
                    .findAll().size()).isEqualTo(2);

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + testTaskBoardEntityA.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        List<TaskBoardEntity> result = taskBoardRepository.findAll();
        assertThat(result.size()).isEqualTo(1); 
        assertThat(result)
                .extracting(TaskBoardEntity::getId)
                .containsExactly(testTaskBoardEntityB.getId());
    }

    @Test
    public void testThatDeleteTaskBoardDeletesTaskGroupsAndTasks() throws Exception{
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.save(testTaskBoardEntityA);

        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(testTaskBoardEntityA);
        taskGroupRepository.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityA);

        assertThat(taskGroupRepository
                    .findAll().size()).isEqualTo(1);
        assertThat(taskRepository
                    .findAll().size()).isEqualTo(1);

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + testTaskBoardEntityA.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository
                    .findAll()).isEmpty();
        assertThat(taskRepository
                    .findAll()).isEmpty();
    }

    // READ -----------------------------------------------------------

    @Test
    public void testThatListTaskBoardsReturnsHttpStatus200() throws Exception{
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.save(testTaskBoardA);

        mockMvc.perform(
            MockMvcRequestBuilders.get("/taskboards")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].taskBoardName").value("Task Board A")
        ); 
    }


    // UPDATE -----------------------------------------------------------

    @Test
    public void testThatUpdateTaskBoardReturnsHttp200() throws Exception{
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA(user);
        taskBoardRepository.save(testTaskBoardA);

        CreateTaskBoardRequestDto testTaskBoardDto = TestTaskBoardData.createTestTaskBoardRequestDtoA();
        testTaskBoardDto.setTaskBoardName("UPDATED");
        String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDto);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/taskboards/" + testTaskBoardA.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskBoardJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskBoardName").value("UPDATED")
        ); 
        
    }
}
