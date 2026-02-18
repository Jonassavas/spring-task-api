package com.jonassavas.spring_task_api.controllers;

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
import com.jonassavas.spring_task_api.domain.dto.task_group.TaskGroupRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.services.TaskGroupService;
import com.jonassavas.spring_task_api.services.TaskService;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskGroupControllerIntegrationTests {
   
    private TaskGroupService taskGroupService;
    private TaskService taskService;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    
    private TaskBoardRepository taskBoardRepository;

    private TaskBoardEntity taskBoard; 

    @Autowired
    public TaskGroupControllerIntegrationTests(MockMvc mockMvc,
                                            TaskGroupService taskGroupService, 
                                            TaskService taskService,
                                            ObjectMapper objectMapper,
                                            TaskBoardRepository taskBoardRepository){
        this.mockMvc = mockMvc;
        this.taskGroupService = taskGroupService;
        this.taskService = taskService;
        this.objectMapper = objectMapper;
        this.taskBoardRepository = taskBoardRepository;
    }
    
    @BeforeEach
    public void setUp(){
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA()
        );
    }

    @Test
    public void testThatCreateTaskGroupReturnsHttp201Create() throws Exception{
        TaskGroupRequestDto testTaskGroupDtoA = TestTaskGroupData.createTaskGroupRequestDtoA(taskBoard);
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/boards/" + taskBoard.getId() + "/groups")
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
            MockMvcRequestBuilders.post("/boards/" + taskBoard.getId() + "/groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskGroupJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("Task Group A")
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskBoardId").isNumber()
        );
    }

    @Test
    public void testThatListTaskGroupsReturnsHttpStatus200() throws Exception{
        mockMvc.perform(
            MockMvcRequestBuilders.get("/boards/" + taskBoard.getId() + "/groups")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatListTaskGroupsReturnsListOfTaskGroups() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);
        
        mockMvc.perform(
            MockMvcRequestBuilders.get("/boards/" + taskBoard.getId() + "/groups")
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$[0].taskGroupName").value("Task Group A")
        );
    }


    @Test
    public void testThatDeleteTaskGroupReturnsHttp204() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);

        assertThat(taskGroupService.findAll().size()).isEqualTo(1); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        assertThat(taskGroupService.findAll().size()).isEqualTo(0); 
    }
    

    @Test
    public void testThatDeleteTaskGroupDeletesCorrectGroup() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);
        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupService.save(testTaskGroupEntityB);
        

        assertThat(taskGroupService.findAll().size()).isEqualTo(2); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        List<TaskGroupEntity> result = taskGroupService.findAll();
        assertThat(result.size()).isEqualTo(1); 
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
    }

    @Test
    public void testThatDeleteTaskGroupDeletesTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityC);

        List<TaskGroupEntity> result = taskGroupService.findAllWithTasks();
        assertThat(result.size()).isEqualTo(1); 
        assertThat(result.getFirst().getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());
        assertThat(taskService.findAll())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
        
        assertThat(taskGroupService.findAllWithTasks().size()).isEqualTo(0);
        assertThat(taskService.findAll().size()).isEqualTo(0);
    }


    @Test
    public void testThatDeleteTaskGroupOnlyDeletesOwnTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityA);
        
        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupService.save(testTaskGroupEntityB);
       
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskService.createTask(testTaskGroupEntityB.getId(), testTaskEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityC(testTaskGroupEntityB);
        taskService.createTask(testTaskGroupEntityB.getId(), testTaskEntityC);

        List<TaskGroupEntity> result = taskGroupService.findAllWithTasks();
        assertThat(result.size()).isEqualTo(2); 
        assertThat(result.get(0).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId());
        assertThat(result.get(1).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityB.getId(),
                                                     testTaskEntityC.getId());
        assertThat(taskService.findAll())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityA.getId(), 
                                                        testTaskEntityB.getId(), 
                                                        testTaskEntityC.getId());

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .contentType(MediaType.APPLICATION_JSON)    
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );

        result = taskGroupService.findAllWithTasks();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskGroupEntity::getId)
                .containsExactly(testTaskGroupEntityB.getId());
        assertThat(result.get(0).getTasks())
                                    .extracting(TaskEntity::getId)
                                    .containsExactly(testTaskEntityB.getId(),
                                                     testTaskEntityC.getId());
    }


    @Test
    public void testThatUpdateTaskGroupReturnsHttp200() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);

        TaskGroupRequestDto testTaskGroupDtoA = TestTaskGroupData.createTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/boards/"+ taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
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
        taskGroupService.save(testTaskGroupEntityA);

        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityA);

        assertThat(taskGroupService.findByIdWithTasks(testTaskGroupEntityA.getId()).getTasks())
                            .extracting(TaskEntity::getId)
                            .containsExactly(testTaskEntityA.getId());

        TaskGroupRequestDto testTaskGroupDtoA = TestTaskGroupData.createTaskGroupDtoA(taskBoard);
        testTaskGroupDtoA.setTaskGroupName("UPDATED");
        String taskGroupJson = objectMapper.writeValueAsString(testTaskGroupDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId())
                .contentType(MediaType.APPLICATION_JSON)   
                .content(taskGroupJson) 
        ).andExpect(
            MockMvcResultMatchers.status().isOk()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskGroupName").value("UPDATED")
        );

        assertThat(taskGroupService.findByIdWithTasks(testTaskGroupEntityA.getId()).getTasks())
                            .extracting(TaskEntity::getId)
                            .containsExactly(testTaskEntityA.getId());
    }


    @Test
    public void testThatDeleteAllTasksDeletesCorrespondingTasks() throws Exception{
        TaskGroupEntity testTaskGroupEntityA = TestTaskGroupData.createTaskGroupEntityA(taskBoard);
        taskGroupService.save(testTaskGroupEntityA);
        TaskEntity testTaskEntityA = TestTaskData.createTestTaskEntityA(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityA);
        TaskEntity testTaskEntityB = TestTaskData.createTestTaskEntityB(testTaskGroupEntityA);
        taskService.createTask(testTaskGroupEntityA.getId(), testTaskEntityB);


        TaskGroupEntity testTaskGroupEntityB = TestTaskGroupData.createTaskGroupEntityB(taskBoard);
        taskGroupService.save(testTaskGroupEntityB);
        TaskEntity testTaskEntityC = TestTaskData.createTestTaskEntityB(testTaskGroupEntityB);
        taskService.createTask(testTaskGroupEntityB.getId(), testTaskEntityC);

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + taskBoard.getId() + "/groups/" + testTaskGroupEntityA.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent()
        );
        
        List<TaskEntity> result = taskService.findAll();
        assertThat(result).extracting(TaskEntity::getId).containsExactly(testTaskEntityC.getId());
    }
}
