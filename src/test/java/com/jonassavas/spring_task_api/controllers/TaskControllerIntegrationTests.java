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
import com.jonassavas.spring_task_api.domain.dto.task.TaskRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.services.TaskGroupService;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;




@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskControllerIntegrationTests {
    
    private TaskGroupService taskGroupService;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private TaskRepository taskRepository;
    private TaskBoardRepository taskBoardRepository;
    private TaskGroupRepository taskGroupRepository;

    // Create taskBoard here, to be used in the tests
    private TaskBoardEntity taskBoard; 
    private TaskGroupEntity taskGroupA;
    private TaskGroupEntity taskGroupB;

    @Autowired
    public TaskControllerIntegrationTests(TaskGroupService taskGroupService, 
                                        MockMvc mockMvc, 
                                        ObjectMapper objectMapper,
                                        TaskRepository taskRepository,
                                        TaskBoardRepository taskBoardRepository, 
                                        TaskGroupRepository taskGroupRepository){
        this.taskGroupService = taskGroupService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.taskRepository = taskRepository;
        this.taskBoardRepository = taskBoardRepository;
        this.taskGroupRepository = taskGroupRepository; 
    } 
    
    @BeforeEach
    public void setUp(){
        taskBoard = taskBoardRepository.save(
            TestTaskBoardData.createTestTaskBoardEntityA()
        );
        taskGroupA = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
        taskGroupB = taskGroupRepository.save(
            TestTaskGroupData.createTaskGroupEntityA(taskBoard)
        );
    }

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
        TaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
        TaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + taskGroupA.getId() + "/tasks")
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
        TaskRequestDto testTaskRequestDtoA = TestTaskData.createTestTaskRequestDtoA(taskGroupA);

        testTaskRequestDtoA.setTaskGroupId(99L);

        String taskJson = objectMapper.writeValueAsString(testTaskRequestDtoA);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups/" + 99 + "/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskJson)
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound()
        );
    }


    @Test
    public void testThatDeleteTaskReturnsHttp204() throws Exception{
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        assertThat(taskGroupService
                    .findByIdWithTasks(taskGroupA.getId())
                        .getTasks().size())
                .isEqualTo(1); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupService
                    .findByIdWithTasks(taskGroupA.getId())
                        .getTasks().size())
                .isEqualTo(0); 
    }

    @Test
    public void testThatDeleteTaskDeletesCorrectTask() throws Exception{
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        TaskEntity testTaskEntityB = taskRepository.save(TestTaskData.createTestTaskEntityB(taskGroupA));

        assertThat(taskGroupService
                    .findByIdWithTasks(taskGroupA.getId())
                        .getTasks().size())
                .isEqualTo(2); 

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/tasks/" + testTaskEntityA.getId())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());
        
        List<TaskEntity> result = taskGroupService
                                    .findByIdWithTasks(taskGroupA.getId())
                                        .getTasks();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .extracting(TaskEntity::getId)
                .containsExactly(testTaskEntityB.getId());
        
    }


    @Test
    public void testUpdateTaskName() throws Exception{
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        TaskRequestDto testRequestTaskDto = TestTaskData.createTestRequestTaskDto(taskGroupA);
        testRequestTaskDto.setTaskName("UPDATED");

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
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
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        // Creating tasks for another taskGroup: B
        TaskRequestDto testRequestTaskDto = TestTaskData.createTestRequestTaskDto(taskGroupB);
        testRequestTaskDto.setTaskGroupId(taskGroupB.getId());

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
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
        TaskEntity testTaskEntityA = taskRepository.save(TestTaskData.createTestTaskEntityA(taskGroupA));

        // Creating request to change testTaskEntityA from taskGroup: A --> B
        TaskRequestDto testRequestTaskDto = TestTaskData.createTestRequestTaskDto(taskGroupB);
        testRequestTaskDto.setTaskGroupId(taskGroupB.getId());
        testRequestTaskDto.setTaskName("UPDATED");

        String taskJson = objectMapper.writeValueAsString(testRequestTaskDto);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/tasks/" + testTaskEntityA.getId())
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
