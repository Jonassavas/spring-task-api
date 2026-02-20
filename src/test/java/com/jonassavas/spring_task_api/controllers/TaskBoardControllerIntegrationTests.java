package com.jonassavas.spring_task_api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService.TestTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonassavas.spring_task_api.domain.dto.task_board.TaskBoardRequestDto;
import com.jonassavas.spring_task_api.domain.entities.TaskBoardEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskEntity;
import com.jonassavas.spring_task_api.domain.entities.TaskGroupEntity;
import com.jonassavas.spring_task_api.repositories.TaskBoardRepository;
import com.jonassavas.spring_task_api.repositories.TaskGroupRepository;
import com.jonassavas.spring_task_api.repositories.TaskRepository;
import com.jonassavas.spring_task_api.services.TaskBoardService;
import com.jonassavas.util.TestTaskBoardData;
import com.jonassavas.util.TestTaskData;
import com.jonassavas.util.TestTaskGroupData;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.print.attribute.standard.Media;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TaskBoardControllerIntegrationTests {

    private TaskBoardService taskBoardService;

    private TaskGroupRepository taskGroupRepository;
    private TaskRepository taskRepository;
    
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private TaskBoardRepository taskBoardRepository;

    // Add user here?

    @Autowired
    public TaskBoardControllerIntegrationTests(TaskBoardService taskBoardService,
                                                MockMvc mockMvc,
                                                ObjectMapper objectMapper,
                                                TaskBoardRepository taskBoardRepository,
                                                TaskGroupRepository taskGroupRepository,
                                                TaskRepository taskRepository){
        this.taskBoardService = taskBoardService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.taskBoardRepository = taskBoardRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskRepository = taskRepository;
    }

    // CREATE -----------------------------------------------------------

    @Test
    public void testThatCreateTaskReturnsHttp201Create() throws Exception{
       TaskBoardRequestDto testTaskBoardDtoA = TestTaskBoardData.createTestTaskBoardRequestDtoA();

       String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

       mockMvc.perform(
        MockMvcRequestBuilders.post("/boards")
        .contentType(MediaType.APPLICATION_JSON)
        .content(taskBoardJson)
       ).andExpect(
        MockMvcResultMatchers.status().isCreated() 
       );
    }
    
    @Test
    public void testThatCreateTaskReturnsSavedTask() throws Exception{
       TaskBoardRequestDto testTaskBoardDtoA = TestTaskBoardData.createTestTaskBoardRequestDtoA();

       String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDtoA);

       mockMvc.perform(
        MockMvcRequestBuilders.post("/boards")
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
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA();
        taskBoardRepository.save(testTaskBoardEntityA);

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .equals(testTaskBoardEntityA));

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + testTaskBoardEntityA.getId())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskBoardRepository
                    .findById(testTaskBoardEntityA.getId())
                    .isEmpty());
    }

    @Test
    public void testThatDeleteTaskBoardDeletesCorrectBoard() throws Exception{
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA();
        taskBoardRepository.save(testTaskBoardEntityA);

        TaskBoardEntity testTaskBoardEntityB = TestTaskBoardData.createTestTaskBoardEntityB();
        taskBoardRepository.save(testTaskBoardEntityB);

        assertThat(taskBoardRepository
                    .findAll().size()).isEqualTo(2);

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/boards/" + testTaskBoardEntityA.getId())
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
        TaskBoardEntity testTaskBoardEntityA = TestTaskBoardData.createTestTaskBoardEntityA();
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
            MockMvcRequestBuilders.delete("/boards/" + testTaskBoardEntityA.getId())
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent());

        assertThat(taskGroupRepository
                    .findAll()).isEmpty();
        assertThat(taskRepository
                    .findAll()).isEmpty();
    }

    // LIST -----------------------------------------------------------

    @Test
    public void testThatListTaskBoardsReturnsHttpStatus200() throws Exception{
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA();
        taskBoardRepository.save(testTaskBoardA);

        mockMvc.perform(
            MockMvcRequestBuilders.get("/boards")
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
        TaskBoardEntity testTaskBoardA = TestTaskBoardData.createTestTaskBoardEntityA();
        taskBoardRepository.save(testTaskBoardA);

        TaskBoardRequestDto testTaskBoardDto = TestTaskBoardData.createTestTaskBoardRequestDtoA();
        testTaskBoardDto.setTaskBoardName("UPDATED");
        String taskBoardJson = objectMapper.writeValueAsString(testTaskBoardDto);

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/boards/" + testTaskBoardA.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(taskBoardJson)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.taskBoardName").value("UPDATED")
        ); 
        
    }
}
