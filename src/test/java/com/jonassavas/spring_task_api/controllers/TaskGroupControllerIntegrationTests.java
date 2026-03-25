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
import com.jonassavas.spring_task_api.domain.dto.task_group.CreateTaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.dto.task_group.UpdateTaskGroupRequestDto;
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
public class TaskGroupControllerIntegrationTests {

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
        A TaskGroup needs a TaskBoard:
            - TaskBoard --> TaskGroup
    */
    private UserEntity user;
    private TaskBoardEntity taskBoard;

    @Autowired
    public TaskGroupControllerIntegrationTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TaskRepository taskRepository,
            UserRepository userRepository,
            TaskBoardRepository taskBoardRepository,
            TaskGroupRepository taskGroupRepository,
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
    }

    // CREATE -----------------------------------------------------------
    @Test
    public void testThatCreateTaskGroupReturnsHttp201Create() throws Exception{
        CreateTaskGroupRequestDto testTaskGroupDtoA = 
                    TestTaskGroupData.createTaskGroupRequestDtoA(taskBoard);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/taskboards/" + taskBoard.getId() + "/groups")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskGroupJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateTaskGroupReturnsSavedTaskGroup() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupEntityA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/taskboards/" + taskBoard.getId() + "/groups")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskGroupJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("Task Group A")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskBoardId").isNumber()
        );
    }

    // READ -----------------------------------------------------------

    @Test
    public void testThatListTaskGroupsReturnsHttpStatus200() throws Exception{
        mockMvc.perform(
            MockMvcRequestBuilders.get("/taskboards/" + taskBoard.getId() + "/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatListTaskGroupsReturnsListOfTaskGroups() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);
        
        mockMvc.perform(
            MockMvcRequestBuilders.get("/taskboards/" + taskBoard.getId() + "/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].taskGroupName")
                .value("Task Group A")
        );
    }

    // DELETE -----------------------------------------------------------

    @Test
    public void testThatDeleteTaskGroupReturnsHttp204() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(1); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(0); 
    }
    

    @Test
    public void testThatDeleteTaskGroupDeletesCorrectGroup() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);
        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityB);
        

        assertThat(taskGroupRepository.findAll().size()).isEqualTo(2); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)    
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        List<TaskGroupEntity> result = taskGroupRepository.findAll();
        assertThat(result.size()).isEqualTo(1); 
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
    }

    @Test
    public void testThatDeleteTaskGroupDeletesTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityC);

        
        List<TaskGroupEntity> result = 
                taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1); 
        assertThat(result.getFirst().getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());
        assertThat(taskRepository.findAll())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)    
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
        
        assertThat(taskGroupRepository.findAllWithTasksByUsername(user.getUsername()).size()).isEqualTo(0);
        assertThat(taskRepository.findAll().size()).isEqualTo(0);
    }


    @Test
    public void testThatDeleteTaskGroupOnlyDeletesOwnTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityA);
        
        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityB); 
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskRepository.save(testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityB);
        taskRepository.save(testTaskEntityC);

        List<TaskGroupEntity> result = taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(2); 
        assertThat(result.get(0).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId());
        assertThat(result.get(1).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityB.getId(),
                                                     testTaskEntityC.getId());
        assertThat(taskRepository.findAll())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        result = taskGroupRepository.findAllWithTasksByUsername(user.getUsername());
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
        assertThat(result.get(0).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityB.getId(),
                                                     testTaskEntityC.getId());
    }

    // UPDATE -----------------------------------------------------------

    @Test
    public void testThatUpdateTaskGroupReturnsHttp200() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);

        CreateTaskGroupRequestDto testTaskGroupDtoA = TestTaskGroupData.createTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/taskboards/"+ taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)   
                .content(taskGroupJson) 
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("UPDATED")
        );
    }


    @Test
    public void testThatUpdateTaskGroupKeepsItsTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityA);

        assertThat(taskGroupRepository
            .findByIdWithTasksAndUsername(testTaskGroupEntityA.getId(), user.getUsername()).get().getTasks())
                            .extracting(TaskEntity::getId)
                            .containsExactly(testTaskEntityA.getId());

        UpdateTaskGroupRequestDto testTaskGroupDtoA = TestTaskGroupData.updateTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)   
                .content(taskGroupJson) 
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("UPDATED")
        );

        assertThat(taskGroupRepository
            .findByIdWithTasksAndUsername(testTaskGroupEntityA.getId(), user.getUsername()).get().getTasks()) 
                            .extracting(TaskEntity::getId)
                            .containsExactly(testTaskEntityA.getId());
    }


    @Test
    public void testThatDeleteAllTasksDeletesCorrespondingTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityA);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskRepository.save(testTaskEntityB);


        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupRepository.save(testTaskGroupEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskRepository.save(testTaskEntityC);

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/taskboards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId() + "/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
        
        List<TaskEntity> result = taskRepository.findAll();
        assertThat(result).extracting(TaskEntity::getId).containsExactly(testTaskEntityC.getId());
    }
}
